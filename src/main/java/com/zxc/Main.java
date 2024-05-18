package com.zxc;


import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.matchers.*;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import com.google.gson.Gson;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.DiffConfiguration;

import java.io.*;
import java.util.*;

//保存本次提交中所有变更方法的文件路径以及变更类型
class ChangedFile {
    String fileNameLong;
    String changedType;

    // 构造方法
    public ChangedFile(String fileNameLong, String changedType) {
        this.fileNameLong = fileNameLong;
        this.changedType = changedType;
    }
}

class MethodScore implements Serializable{
    String methodName;
    int index;
    double astScore;
    double HV, PCOM;
    int CC, LOC;
    double weight;
    double DDG_impact, CDG_impact;
    double CM, IR;

    double astScore_normal;
    double HV_normal, PCOM_normal;
    int CC_normal, LOC_normal;
    double weight_normal;
    double DDG_impact_normal, CDG_impact_normal;
    double CM_normal, IR_normal;
    double scoreBeforeNormalize, scoreAfterNormalize;

    public MethodScore(String methodName, int index, double astScore,int LOC, int CC, double HV, double PCOM,double weight, double DDG_impact, double CDG_impact) {
        this.methodName = methodName;
        this.index = index;
        this.astScore = astScore;
        this.HV = HV;
        this.PCOM = PCOM;
        this.CC = CC;
        this.LOC = LOC;
        this.weight = weight;
        this.DDG_impact = DDG_impact;
        this.CDG_impact = CDG_impact;
    }
}
class CommitScore implements Serializable{
    String commitHash;
    List<MethodScore> methodScore = new ArrayList<>();

    double scoreOfCommitBefore, scoreOfCommitAfter;
    public CommitScore(String commitHash, List<MethodScore> methodScore) {
        this.commitHash = commitHash;
        this.methodScore = methodScore;
    }
}

public class Main {
    public static void main(String args[]) throws Exception {
        // 记录程序开始时间
        long startTime = System.currentTimeMillis();
        long lastTime = startTime;

        // 创建文件输出流
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("DeveloperContributionEvaluation/output.log", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        // 创建打印流，指向文件输出流
        PrintStream filePrintStream = new PrintStream(fileOutputStream);
        // 将System.out重新定向到文件打印流
        System.setOut(filePrintStream);
//----------------------------------------------------------------------------------------------------------------------

        Tool tool = new Tool();

        String gitDirectory = "E:/Postgraduate_study/commons-release-plugin/";
        String projectName = "commons-release-plugin";
        String[] commits = {
                "5b64f485e1340c2aef6bc446ee2fde6349c4c1df",
                "b16611cecea8b10a12c81ee00fc52d5126143722",
                "80375b75133ee8f12c65cbf9e2963e5e6ed60cd0",
                "1270c8e90eef88474726c65f908e3d69ce18a465",
                "36b02c2cba36637669c6faf680932a23a540fc0a",
                "1c3468d72202d957e4a30203ba43d9456ffc54cf",
                "dfc860468bf028a1ac762473e03872b1b7dbb1b6",
                "cd3769ce780261eb19d95171997d38fda8661455",
                "2a74c41ff599b704f9009de56e946d3358ca7219",
                "9ba4677b42255a6c91efca764895d7e81a601aa9",
                "cf379d1ff144236d12ef7b9cdf378265a425d954",
                "b176601de87bfd2a2a04a08868081787be11b122",
                "d5dfc90057b6c6cce216e56f5e29684208b88d92",
                "b5fcd34e2730a400712203b3ccdc24a3e1ff48af",
                "6887392841651bbe1d126ddb45c5e41e6c2c5bd4",
                "a1e5396e968938790489a5eefb2c7d5fe7994c28",
                "b270424ad79370e29b757ed1158cf12d83d05e9f",
                "840d4f66fff2457847b7311c2119cf573bf52995",
                "0c013489a523096b14d5e65339761ce8feafda75",
                "cac3ab9d291b760fb9a4e7fa4457bc87d736bf3f",
                "0629c6f53d8744c69fc7e636e91db627031736a1",
                "fd3e20a4764014db5677ff1c52625b3b0f02922f",
                "250f9f77ee493484b2179d8f3456ed52ad263eab",
                "2b159df9712c8186b66821f0de613ddc831c6a75",
                "60f4369518260db4d8af5d3207b487d6d1e0ab29",
                "9d0ef04f3ce00afee509328772ec1f39e62da759",
                "d0bf57f2a122d2a8b4a5309cc65d4ea47ad49c21",
                "9e88aeae179c01cad1c9e0c94b9d9b0ed5ec3f24",
                "9dfa6b3a872d58590f18f8f925f4f8e3afedb7b1",
                "d1499ca1510db7248d2cee838c4e329df14c3d4e",
                "e0b1afcd69923b75caa09717db9da0fd688d0654",
                "900542543cc6911304495bbd825b4dc044899c49",
                "b2cadef95facdfd76eaf8a4041ea9bfcf71b30f9",
                "818a2ce936fbd82cb16da45f5505628e76cccc82",
                "b71a2a1f9256dd6c7a4f86015e0081a7472660bd",
                "7651f5bc32de10f8e5d9014ca8d39c16d9c417dc",
                "462dffabe3c0c01a99fea2dffdc59067059cea02",
                "c315ce0cee4f3477a9af1070f06b7285e1694c0a",
                "7505912b803ad4394ceb4ef70dd866d20b2627e5",
                "55d3a85d41bc1678fbd387712e1053a6e3950bf7",
                "2543f323dca0aeecf023a11b3cab295871851ee9",
                "225e6f1be73ea3695680a21dbb8b85b24bcba4cb",
                "d59a1c6f6ec5fbe3fb8bb0bdc4d885512d07b51c",
                "e6b5cbcf5ada01faa469abb78e2fcd465ca26725",
                "731a2893803ab57f3eef2e5ac9e02fcd0aa8214d",
                "a5bfc15a43a708a6eadac3b65e1745a1b122bc8e",
                "041d0287331033bcbe2e749a95397fde26d0d426",
                "fc71aa2ef3ea82461cbf10bc9c32c584b6e4fa99",
                "bd02fb6b923c054726f801cf9bfa0507e6c2f369",
                "814c5eab3f746b40b44c430e2585ea9e98e67adb",
                "b96fa91313cae6ebe11e799c94ec1115f93e1b33",
                "1900a67fb694c655542350cba444fef85aa2121e",
                "812ea8f5150124756c991632ead795202e72197d",
                "3b775f6f6a8d4a27aabafea7476084dd5b171a3d",
                "4cda015b5e088dd0d187c507e1982bbb9ee37dba",
                "cd8911a70c6af6284e7e13113369829c218b6972",
                "6491f51f4c165c1e9e2fcaf2a4d536c4b3955834",
                "36408526edf8a6910ea5fbabe168aa0f1d039938",
                "3a28d286a709028aaa2232f3664ee189edce3ca9",
                "ab1397a500a21412f18d9da43c37646392158d3f",
                "7a90d9875a9f86611dfc415d276f8386a8659f03",
                "8a4c5a37df3bdd3a489c04a536f1b695831fab7f",
                "90959a095abeb7950dd27224fc0f9a0446f8061b",
                "77dd4b425e8d511819960a1827994ccab2f95e4e",
                "c476f024e1f2e7361877f7814a1d6a3db597ee82",
                "c0c8fb3dfea13a16e11211c7c5d67741dbf07f80",
                "0cae8c664df58dca21d8e415a103ab301a81f041",
                "66da516386b470693cf7bcbac738ba913072d04e",
                "0721684648e6856c1e9d59afd5570b6beeb4fe39",
                "8c1a2483453a22012f7f2b6ffe45efed1853d6ff",
                "c6d7168bd8d3d3232ffecf8bb064572dccd5657d",
                "0c29ac3a1f030db55fbc78796516251f9c9c608c",
                "4692d2b62a19cd649b93c6f155544f5f5630d842",
                "eff5266a54065144f316e3e8d66c63192e4af6d5",
                "d92c25ac64cc371a447692731233479ca98b31af",
                "0c14b25f200a6854a5912a64ff3db000b3a66158",
                "34bf87ee58e6856266006051eb7e6d7d179986f0",
                "0cbef20602226e8591e5d6aa39b6ac5a5a6daa1b",
                "740556589adb94d0af97e017850a945f474cff8d",
                "9ce2be1fc28de3708fc84858dd51d56e022e1c30",
                "20ed13cd7aa90f4fd88711feee6f064bb108a9b7",
                "5082cd05fef5baa24e905cb2d701f8c0939610d7",
                "1feed287dfc441d432fd75a9a0b08280d1d46d84",
                "5db46c8c73c1331465f512dbc59ca0677759b6c5",
                "b7b204cc21639062135fd14417d8eda567b70d89",
                "46ed2ce15e6377c989e0bbc0fd6e4cb203bcd639",
                "b341fce6eaa780cd1ea096c57a04becaa0ba22ff",
                "ceecf0ee7dd7218ec2281589f118e61b93f94183",
                "55859e425e61854ced1b12727208bf34f012eea0",
                "f4002b84e7f6c0f336e49141e1e221b1e9043adb",
                "9283b74770fa03875b08f3f4a89f2ad397c98e1a",
                "e46ed7dc654ed8579d6d1556a8d1e08e107ae939",
                "29c3d2d620c576fb8cad18798ed5ef32a43c8e27",
                "63068ef85280c15243a37821981ccbc5c5277f22",
                "9e2ebcd71da40965171fae82a4adef393a369b95",
                "e3ce9a1ad8eaf38405ccf01ff5729dc4a171468f",
                "8af3b4c0a269db99c09313c076b154de7a09b184",
                "c3745c4219b6d8418432c2ea8512bb17472780d1",
                "a817ab95a087561541b9513caf986de1c5b2efc6",
                "79019d2c66e725b1fb0595c92d5f2331c9b5a575",
                "ad9c69f377e1056c18273866eab3973e05afcec4",
                "f37d774f90fabb6afd268b0a4912edd4afe88e07",
                "931dc52788e8ae5db850068c64a2f3acfb0b2b03",
                "6086508ddc61b1e3677fb6664c5a78d03286ec57",
                "2aca8431bbeb324531d6ddc99b1998b35307fb55",
                "13714f7f99a09c979947feadbf99e81e961fc388",
                "0d49b4704debd8a561926b26fa18d658774e98e1",
                "a1cbc9dd987aa1a599a8f7ba0547b30c02a8bdb1",
                "701579fd3d04bca40ff446ee12f2198f2dff75b8",
                "d60d88dd936564124f1a69a909b5935bacf13713",
                "034a25ab725c5c08a9a2070376ac2039af255592",
                "a661fe800fd068ccab8e8e0d3aa06124961cf576",
                "93a78f5bcfd62ce2eab80a0ddadcc089884253bf",
                "d2c28a5053be5c1f214c4ae44c2134a72af1b019",
                "804be00c02f0539ccbe9bd8331a0d96967a7cc14",
                "4f5d14e3d930c074e2bf78267041131e4938d0b9",
                "7e4d84408e95aed110b7a34364b29be12069831b",
                "7cce8cb29d76e6642e65d67a44f647c20d80bbb7",
                "3ff3a7cb23c1114f415220b129bc560551230d58",
                "21e282ae64c28d1ffdd5d00c93f34bfd932db58e",
                "7b1b50f4b0eaadebcb46cdcd2c79ac042e50845f",
                "9a2db79a9e514afd8d7b7c8964166a0086f136d0",
                "ec846af9085cad8f42304b185f5bb5666ce33af5",
                "ea2fb4e75cf8bdebc60676e1f610647e234da448",
                "919f44a44780ea7f1e4062054f01e79fcb698130",
                "d8b0e0e74ba2fb174e26b33f8358f70c00a55138",
                "0569fd5744769c37cef42be90af7643981b5b198",
                "2f945be958256e4bd4e87ee09ab52a346b97c12b",
                "5d4ab8c4c3b024aae607d83f428be7cbb17568e5",
                "bb999955f1ac5609d7a5c9ba5df93f883ffffa91",
                "977f9bb1754c4c925da650a9a3af8cab926fb4d9",
                "b741c5b0b933494a083254483d009e86e091503f",
                "097b0283a8c3c08f90f1f7e0f13489e4c8652fc6",
                "0433b1e0cb93f82f26561a3c98d9c5cd0a93919f",
                "e1f50b99d059d0571cb4e200a40877434d535e6a",
                "ea0d256d50eb3be306f90ece61f40cb2df85341b",
                "af1880079c500cc32bbd5ed6d2923c263563047f",
                "d1d0cb407eea63d73a22c21b5add9eee34602539",
                "d365f0b08d6e01664b3164bf9e48bdb0aba87dd0",
                "2a3a88238a95397331e35fb98670240575a4bea9",
                "7cc0bd923a60bbb21aaabcae8e96da04d516f577",
                "195387875df82a1f628c0e2eda8d737a7518bf1f",
                "e0b91288f4202f5b223b6f9251b4abe9f7889497",
                "9923a5a39a03d70dcefb2b76cdfbef5b294dfabf",
                "a0b4bc2ce5c9ef25ac59fe177ffdc1a9ef216b29",
                "75c923080dc16ace9866389a906188ae71b522c4",
                "f0c5441e4240478fea6500c0ef2dc95b86fe9ec5",
                "22442eca298929215de04ca51f7db51369566adb",
        };

        List<CommitScore> commitScore = new ArrayList<>();
        int index = 0;
        for(String newCommit:commits) {
            String oldCommit = tool.executeGitCommand(gitDirectory, new String[]{"git", "log", "--format=%H", "--skip=1", "-n", "1", newCommit}).replace("\n", "");//获取当前版本的上一个版本
            List<MethodScore>methodScore = calculateCommitScore(gitDirectory, projectName, newCommit, oldCommit, index);
            index += methodScore.size();
            commitScore.add(new CommitScore(newCommit, methodScore));
        }
        normalize(commitScore);

//----------------------------------------------------------------------------------------------------------------------
        // 记录程序结束时间
        long endTime = System.currentTimeMillis();

        // 计算总运行时间（毫秒）
        long totalTimeInMillis = endTime - startTime;

        // 将毫秒转换为秒
        double totalTimeInSeconds = totalTimeInMillis / 1000.0;

        System.out.println("程序总运行时间（秒）：" + totalTimeInSeconds);

        // 关闭文件输出流
        try {
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void normalize(List<CommitScore> commitScore) {
        try {
            // 使用 Gson 将对象转换为 JSON 格式
            Gson gson = new Gson();
            String json = gson.toJson(commitScore);

            // 将 JSON 字符串传递给 Python 脚本的标准输入
            ProcessBuilder pb = new ProcessBuilder("python", "DeveloperContributionEvaluation/pythonScripts/normalizeAndGetFinalScore.py");
            Process p = pb.start();
            OutputStream outputStream = p.getOutputStream();
            outputStream.write(json.getBytes());
            outputStream.close();

            // 读取 Python 脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<MethodScore> calculateCommitScore(String gitDirectory, String projectName, String newCommit, String oldCommit, int index) throws Exception {
        // 记录程序开始时间
        long startTime = System.currentTimeMillis();

        List<MethodScore>methodScore = new ArrayList<>();
        Tool tool = new Tool();
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.println("\n@@@nowCommitHash = " + newCommit);
        System.out.println("@@@oldCommitHash = " + oldCommit);
        tool.executeGitCommand(gitDirectory, new String[]{"git", "checkout", newCommit});//切换到当前版本

        List<ChangedFile> changedJavaFiles = getChangedJavaFiles(gitDirectory, newCommit, oldCommit); //获取所有发生更改的.java文件的路径和变更类型
        Map<String, String> fileChangedType = getFileChangedType(changedJavaFiles);//再变更文件名称与变更类型间建立映射

//        getEditScriptsBetweenCommits(gitDirectory, newCommit, oldCommit, changedJavaFiles); //获取两个commit之间所有发生更改的.java文件的编辑脚本以及每个文件的AST
        String editscriptsPath = "DeveloperContributionEvaluation/editScripts/" + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/"; //这些编辑脚本的保存路径
        ASTScoreCalculator astScoreCalculator = new ASTScoreCalculator();

        Map<String, Double> astScore= astScoreCalculator.calculateTotalASTScore(editscriptsPath, newCommit, oldCommit, fileChangedType); //计算每个变更方法的ast得分


//        以上计算AST编辑脚本分数
//        ------------------------------------------------------------------------------------------------------------------------------------

        CallGraph callGraph = new CallGraph();
        String analyzedDirectory = gitDirectory + "src/";
        String outputFormat = "dot";
        String granularity = "method";
//        System.out.println(analyzedDirectory);
//        callGraph.getCallGraph(analyzedDirectory, outputFormat, granularity, projectName, newCommit); //获取该项目的调用图
        System.out.println("调用depends完成");
        String callGraphName = projectName + "_" + newCommit.substring(0, 7) + "-" + granularity + "." + outputFormat;
        String callGraphPath = "DeveloperContributionEvaluation/CallGraphs/" + callGraphName;


        Map<Integer, List<Integer>> graphC = callGraph.buildGraph(callGraphPath);//根据调用图生成正向邻接表
        Map<Integer, List<Integer>> backwardGraphC = callGraph.buildBackwardGraph();//根据调用图生成反向邻接表
        Map<Integer, Double> mapPr = callGraph.getPageRank(graphC, backwardGraphC);


        Map<Integer, Double> nodeWeight = callGraph.measureInterFunctionInteraction(graphC, mapPr, 1);

//        for (Map.Entry<Integer, Double> entry : nodeWeight.entrySet()) {
//            System.out.println("Node " + entry.getKey() + ": weight = " + entry.getValue());
//        }

        //获取项目中每个函数在调用图中所对应的节点编号
        Map<String, Integer> methodToNodeMap = callGraph.getNodeMapping("DeveloperContributionEvaluation/CallGraphs/" + callGraphName);

//        以上计算调用图中各节点的权重
//        ------------------------------------------------------------------------------------------------------------------------------------

        ComplexityCalculator complexityCalculator = new ComplexityCalculator();
        complexityCalculator.getChangedMethods_LOC_CC_HV_PCom(gitDirectory, oldCommit, newCommit);
        List<String> changedMethods = complexityCalculator.getChangedMethods();
        Map<String, Integer> LOC = complexityCalculator.getLOC();
        Map<String, Integer> CC = complexityCalculator.getCC();
        Map<String, Double> HV = complexityCalculator.getHV();
        Map<String, Double> PCom = complexityCalculator.getPCom();
        Map<String, Double> CM = new HashMap<>();

//        以上计算各修改函数的复杂性度量(提取了各修改函数的内容)
//        ------------------------------------------------------------------------------------------------------------------------------------
        DDG ddg = new DDG();
        CDG cdg = new CDG();
        //记录获取DDG的开始时间
        long DDGstartTime = System.currentTimeMillis();
//        ddg.getDDG("E:/IDEA/maven-project/DeveloperContributionEvaluation/changedFilesContent/" + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/", newCommit);
        // 记录获取DDG结束时间
        long DDGendTime = System.currentTimeMillis();
        double totalDDGTime = (DDGendTime - DDGstartTime) / 1000.0;
        System.out.println("本次getDDG计算的运行时间（秒）：" + totalDDGTime);

        //记录获取CDG的开始时间
        long CDGstartTime = System.currentTimeMillis();
//        cdg.getCDG("E:/IDEA/maven-project/DeveloperContributionEvaluation/changedFilesContent/" + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/", newCommit);
        // 记录获取CDG结束时间
        long CDGendTime = System.currentTimeMillis();
        double totalCDGTime = (CDGendTime - CDGstartTime) / 1000.0;
        System.out.println("本次getCDG计算的运行时间（秒）：" + totalCDGTime);

        Map<String, Double> DDG_impact = new HashMap<>();
        for(String method:changedMethods) {
            String[] tmp = method.split(":");
            String methodName = tmp[tmp.length-1];
            String className = method;
            if(tmp.length > 1)
                className.replace("::" + methodName, "");

            DDG_impact.put(method,
                    ddg.getDDGimpact("E:/IDEA/maven-project/DeveloperContributionEvaluation/PDGs/" + newCommit.substring(0, 7), className, methodName));
        }

        Map<String, Double> CDG_impact = new HashMap<>();
        for(String method:changedMethods) {
            String[] tmp = method.split(":");
            String methodName = tmp[tmp.length-1];
            String className = method;
            if(tmp.length > 1)
                className.replace("::" + methodName, "");
            CDG_impact.put(method,
                    cdg.getCDGimpact("E:/IDEA/maven-project/DeveloperContributionEvaluation/PDGs/" + newCommit.substring(0, 7),className, methodName));
        }

        double scoreOfCommit = 0.0;

        for (String method : changedMethods) {
            CM.put(method, (LOC.get(method) + CC.get(method) + HV.get(method) - PCom.get(method)) / 2 + 1);
            if(!astScore.containsKey(method))
                astScore.put(method, 0.0);
//            System.out.println("astScore = " + astScore.get(method));
//            System.out.println("HV = " + HV.get(method) + ", CC = " + CC.get(method) + ", LOC = " + LOC.get(method)
//                    + ", PCom = " + PCom.get(method) + ", CM = " + CM.get(method));

            double weight = 0.0;
            if(methodToNodeMap.containsKey(method) && nodeWeight.containsKey(methodToNodeMap.get(method)))
                weight = nodeWeight.get(methodToNodeMap.get(method));

//            System.out.println("weight = " + weight);

            double IR = 1 + Math.sqrt(DDG_impact.get(method)) + Math.sqrt(CDG_impact.get(method));
//            System.out.println("DDG_impact = " + DDG_impact.get(method) + " , CDG_impact = " + CDG_impact.get(method) + ", IR = " + IR);

            scoreOfCommit += astScore.get(method) * CM.get(method) * (weight + 1) * IR;

//            System.out.println();
            methodScore.add(new MethodScore(method, index++, astScore.get(method), LOC.get(method), CC.get(method), HV.get(method), PCom.get(method), weight, DDG_impact.get(method), CDG_impact.get(method)));
        }
//        System.out.println("commitHash = " + newCommit.substring(0,7) + " , scoreOfCommit = " + scoreOfCommit);

        // 记录程序结束时间
        long nowTime = System.currentTimeMillis();
        // 计算总运行时间（毫秒）
        long totalTimeInMillis = nowTime - startTime;
        // 将毫秒转换为秒
        double totalTimeInSeconds = totalTimeInMillis / 1000.0;

        System.out.println("本次commit计算的运行时间（秒）：" + totalTimeInSeconds);
        return methodScore;
    }

    public static Map<String, String> getFileChangedType(List<ChangedFile> changedJavaFiles) {
        Map<String, String> fileChangedType = new HashMap<>();
        for (ChangedFile file : changedJavaFiles) {
            int lastSlashIndex = file.fileNameLong.lastIndexOf('/');
            String fileName = file.fileNameLong.substring(lastSlashIndex + 1).replace(".java", "");
            fileChangedType.put(fileName, file.changedType);
        }

        return fileChangedType;
    }

    // 遍历一个项目的所有commit
    public static void traverseAllCommits(String gitDirectory, String projectName) throws Exception {
//        // 记录程序开始时间
//        long startTime = System.currentTimeMillis();
//        long lastTime = startTime;

//        String gitDirectory = "E:/Postgraduate_study/FlappyBird";
//        String projectName = "FlappyBird";
        Tool tool = new Tool();
        String latestCommit = tool.executeGitCommand(gitDirectory, new String[]{"git", "log", "--format=%H", "-n", "1", "origin"}).replace("\n", "");
//        System.out.println(latestCommit);
        tool.executeGitCommand(gitDirectory, new String[]{"git", "checkout", latestCommit});//切换到最新的版本

        List<String> commits = getAllCommitHashes(gitDirectory);//传入 Git 项目的目录路径，获取该项目所有的commit版本
        Collections.reverse(commits);

//        String newCommit = commits.get(commits.size()-3);
//        String oldCommit = commits.get(commits.size()-2);
//        String file1 = "E:/IDEA/maven-project/DeveloperContributionEvaluation/changedFilesContent/d7b64a5_to_5e5ba4b/old_Bird.java";
//        String file2 = "E:/IDEA/maven-project/DeveloperContributionEvaluation/changedFilesContent/d7b64a5_to_5e5ba4b/new_Bird.java";
//        getASTFromFile(file2, newCommit);
//        computeEditScript(file1, file2, "", "");
//        gumtreeSpoonASTDiff(file, file2);
//        readEditScriptFile();

        String oldCommit = "0000000";
        for(String newCommit : commits) {
//            String oldCommit = "5676508a17ede2cbb30ee2d6ff23bf7db071f625";
//            String newCommit = "eb44ec32e3a6fd5fa13da512fe03e598aaf18d20";

//            double scoreOfCommit = calculateCommitScore(gitDirectory, projectName, newCommit, oldCommit);
            oldCommit = newCommit;

//            // 记录程序结束时间
//            long nowTime = System.currentTimeMillis();
//            // 计算总运行时间（毫秒）
//            long totalTimeInMillis = nowTime - lastTime;
//            // 将毫秒转换为秒
//            double totalTimeInSeconds = totalTimeInMillis / 1000.0;
//            System.out.println("本次commit计算的运行时间（秒）：" + totalTimeInSeconds);
//            lastTime = nowTime;


//            if(newCommit.equals("5e5ba4bf131b5998c33474ebe34ac7e9d86187ad"))
//                break;
        }
        tool.executeGitCommand(gitDirectory, new String[]{"git", "checkout", latestCommit});//切换到最新的版本
    }
    //生成两个commit之间有变化的.java文件之间的编辑脚本
    public static void getEditScriptsBetweenCommits(String gitDirectory, String newCommit, String oldCommit, List<ChangedFile> changedJavaFiles) throws Exception {
//        System.out.println("这两个commit之间所有的编辑脚本如下：");
        String filePath = "DeveloperContributionEvaluation/changedFilesContent/" + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/";
        // 确保文件夹存在，如果不存在则创建它
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        int cnt = 0;
        for(ChangedFile changedFile:changedJavaFiles){
            String fileNameLong = changedFile.fileNameLong;
//            System.out.println(fileNameLong);
            // 获取两个版本中文件的内容
            String fileContentAtNewCommit = getFileContentAtCommit(gitDirectory, fileNameLong, newCommit);
            String fileContentAtOldCommit = getFileContentAtCommit(gitDirectory, fileNameLong, oldCommit);

//            if(fileContentAtNewCommit.equals("FileNotExist") || fileContentAtOldCommit.equals("FileNotExist")){
//
//                // 计算新增文件或删除文件的分数(将不存在的文件内容设为空即可)
//                System.out.println("跳过" + ++cnt);
//                continue;
//            }
            // 获取最后一个斜杠的索引
            int lastSlashIndex = fileNameLong.lastIndexOf("/");
            String fileNameShort = fileNameLong.substring(lastSlashIndex + 1); //从文件路径中提取出文件名

//            System.out.println("old" + fileContentAtOldCommit);
//            System.out.println(++cnt);
//            System.out.println("成功获取两个commit中文件" + fileNameShort + "的内容");


            //将该文件内容保存到临时文件夹changedFilesContent中
            String newFileName = filePath + "new_" + fileNameShort;
            String oldFileName = filePath + "old_" + fileNameShort;
            writeStringToFile(fileContentAtNewCommit, newFileName);
            writeStringToFile(fileContentAtOldCommit, oldFileName);

            if(changedFile.changedType.equals("A"))// 该文件修改类型为新增
                getASTFromFile(newFileName, newCommit);
            else if(changedFile.changedType.equals("D")) //该文件修改类型为删除"D"或修改"M"
                getASTFromFile(oldFileName, oldCommit);
            else //该文件修改类型为修改"M"
            {
                getASTFromFile(newFileName, newCommit);
                getASTFromFile(oldFileName, oldCommit);
            }
            computeEditScript(oldFileName, newFileName, oldCommit, newCommit);
        }
        System.out.println("所有编辑脚本获取结束");

    }

    //把String写入特定文件
    public static void writeStringToFile(String content, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 检查文件是否存在于提交中
    public static boolean fileExistsInCommit(String gitDirectory, String fileNameLong, String commitHash) {
        String[] command = {"git", "ls-tree", "--name-only", "-r", commitHash};
        String filesInCommit = new Tool().executeGitCommand(gitDirectory, command);
        return filesInCommit.contains(fileNameLong);
    }

    //获取一个commit中指定文件的内容
    public static String getFileContentAtCommit(String gitDirectory, String fileNameLong, String commitHash) {
        // 检查文件是否存在
        if (!fileExistsInCommit(gitDirectory, fileNameLong, commitHash)) {
//            System.err.println("文件 " + fileName + " 不存在于提交 " + commitHash);
//            return "FileNotExist"; // 返回字符串 "FileNotExist"
            int lastSlashIndex = fileNameLong.lastIndexOf("/");
            String fileNameShort = fileNameLong.substring(lastSlashIndex + 1); //从文件路径中提取出文件名
            return "class " + fileNameShort.replace(".java", "") + "{}";// 若该文件不存在，则认定该文件内容为空类
        }
//        System.out.println("成功获取该文件的内容：" + fileName);
        String[] command = {"git", "show", commitHash + ":" + fileNameLong};
        String fileContent = Tool.executeGitCommand(gitDirectory, command);
        return fileContent;
    }

    //获取项目所有commit的版本号
    public static List<String> getAllCommitHashes(String gitDirectory) {
        List<String> commitHashes = new ArrayList<>();

        try {
            // 创建 ProcessBuilder 对象，执行 git log 命令获取提交历史
            ProcessBuilder builder = new ProcessBuilder("git", "log", "--format=%H");
            builder.directory(new File(gitDirectory));

            // 启动进程
            Process process = builder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                commitHashes.add(line); // 将每行的提交哈希值添加到列表中
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("getAllCommitHashes命令执行失败，返回码：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return commitHashes;
    }

    //获取两个commit之间所有有更改的.java文件的名称和更改类型
    public static List<ChangedFile> getChangedJavaFiles(String gitDirectory, String newCommit, String oldCommit) {
        List<ChangedFile> changedFiles = new ArrayList<>();

        try {
            // 创建 ProcessBuilder 对象，执行 git diff --name-only 命令获取文件变更列表
            ProcessBuilder builder;
            if (oldCommit.equals("0000000")) {
                builder = new ProcessBuilder("git", "show", "--name-only", newCommit);
            } else {
//                builder = new ProcessBuilder("git", "diff", "--name-only", oldCommit, newCommit);
                builder = new ProcessBuilder("git", "diff", oldCommit, newCommit, "--name-status", "--", "*.java");
            }

            builder.directory(new File(gitDirectory));//设置命令工作目录

            // 启动进程
            Process process = builder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
//            System.out.println("发生变更的.java文件如下：");
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                // 使用制表符分割字符串
                String[] parts = line.split("\t");

                // 获取changedType和fileNameLong
                String changedType = parts[0];
                String fileNameLong = parts[1];
                changedFiles.add(new ChangedFile(fileNameLong, changedType));
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("getChangedJavaFiles命令执行失败，返回码：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return changedFiles;
    }

    //列出一个文件夹下的所有.java文件
    public static void listJavaFiles(File folder) throws Exception {
        // 检查文件夹是否存在
        if (folder.exists() && folder.isDirectory()) {
            // 获取文件夹中的所有文件和子文件夹
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归调用，处理子文件夹
                        listJavaFiles(file);
                    } else if (file.getName().endsWith(".java")) {
                        // 找到的 .java 文件路径
                        String filePath = file.getAbsolutePath();
                        System.out.println(filePath);
//                        getASTFromFile(filePath);
                    }
                }
            }
        } else {
            System.out.println("指定的路径不是一个文件夹或文件夹不存在。");
        }
    }

    //读文件，用于读取编辑脚本文件
    public static void readEditScriptFile(String s)throws IOException{
        try (RandomAccessFile raf = new RandomAccessFile("E:/IDEA/maven-project/DeveloperContributionEvaluation/src/main/java/com/zxc/Test1.java", "r")) {
//            System.out.println("Initial position: " + raf.getFilePointer());  // 初始位置

            int st = 15, ed = 19;
            raf.seek(st); // 定位到99
            byte[] b = new byte[1024];
            int hasRead = 0;
            for (int i = st; i < ed; i++) {
                char a = (char)raf.readByte();
                System.out.print(a);
            }

//            while ((hasRead = raf.read(b)) > 0) { // 正常读，和InputStream没有任何区别
//                System.out.println(new String(b, 0, hasRead));
//            }
        }
    }

    //从源代码提取出抽象语法树，传入需要分析的.java文件的路径，和当前的commit号
    public static void getASTFromFile(String filePath, String commitHash) throws Exception {
        try {
            Run.initGenerators(); // registers the available parsers
            TreeContext tc = TreeGenerators.getInstance().getTree(filePath); // retrieves and applies the default parser for the file
            Tree t = (Tree) tc.getRoot(); // retrieves the root of the tree
//            System.out.println(t.toTreeString()); // displays the tree in our ad-hoc format
//            System.out.println(TreeIoUtils.toLisp(tc).toString()); // displays the tree in LISP syntax

            // AST 的字符串表示
            String astString = t.toTreeString();

            String astFolder = "DeveloperContributionEvaluation/ASTfiles/" + commitHash.substring(0,7) + "/";

            // 确保文件夹存在，如果不存在则创建它
            File folder = new File(astFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            // 指定要写入的文件名和路径
            String srcFileName = new File(filePath).getName();
            String astFileName = astFolder + srcFileName.replace(".java", "").replace("new_", "").replace("old_", "") + "_AST.txt";

            // 将 AST 字符串写入文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(astFileName))) {
                writer.write(astString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
//            System.err.println("An error occurred while processing the file: " + e.getMessage());
        }
    }

    public static void gumtreeSpoonASTDiff(String filepath1, String filepath2) throws Exception {
        AstComparator comp = new AstComparator();

        //We define a DiffConfiguration
        DiffConfiguration diffConfiguration = new DiffConfiguration();
        //Set the matcher to be used
        diffConfiguration.setMatcher(new CompositeMatchers.ClassicGumtree());
        //Set values for hyperparameters
        GumtreeProperties properties = new GumtreeProperties();
        properties.tryConfigure(ConfigurationOptions.bu_minsim, 0.2);
        properties.tryConfigure(ConfigurationOptions.bu_minsize, 600);
        properties.tryConfigure(ConfigurationOptions.st_minprio, 1);
        //properties.tryConfigure(ConfigurationOptions.st_priocalc, size);
        diffConfiguration.setGumtreeProperties(properties);

        //Now, compute the diff with the configuration diffConfiguration
        File file1 = new File(filepath1);
        File file2 = new File(filepath2);
        Diff resultClassicMatcher = comp.compare(file1, file2, diffConfiguration);
        System.out.println(resultClassicMatcher.toString());
    }

    //生成两个java源文件之间的编辑脚本，file1为旧版本，file2为新版本
    public static void computeEditScript(String file1, String file2, String oldCommit, String newCommit) throws IOException{
        Run.initGenerators(); // registers the available parsers
        String srcFile = file1;
        String dstFile = file2;
        try {
            Tree src = TreeGenerators.getInstance().getTree(srcFile).getRoot(); // retrieves and applies the default parser for the file
            Tree dst = TreeGenerators.getInstance().getTree(dstFile).getRoot(); // retrieves and applies the default parser for the file
            Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
            MappingStore mappings = defaultMatcher.match(src, dst); // computes the mappings between the trees
            EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
            EditScript actions = editScriptGenerator.computeActions(mappings); // computes the edit script
//            for (Action action : actions) {
//                System.out.println(action);
//            }

            String filePath = "DeveloperContributionEvaluation/editScripts/";
            if(!oldCommit.equals("")){
                filePath = filePath + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/";
                // 确保文件夹存在，如果不存在则创建它
                File folder = new File(filePath);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
            }
            String editScriptFileName = filePath + "editscript_" +
                    new File(srcFile).getName().replace(".java", "").replace("old_", "") + ".txt";
            writeEditScriptToFile(actions, editScriptFileName);
        } catch (Exception e) {
//            System.err.println("An error occurred while processing the file: " + e.getMessage());
            // 这里可以添加日志记录或其他错误处理逻辑
        }

    }

    //将编辑脚本写入文件
    private static void writeEditScriptToFile(EditScript actions, String fileNameLong) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileNameLong))) {
            // 将编辑脚本的字符串表示写入文件
            for (Action action : actions) {
                writer.write(action.toString());
                writer.newLine();
            }
//            System.out.println("Edit script written to: " + fileNameLong);
            System.out.println(fileNameLong);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
