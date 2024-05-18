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

        String gitDirectory = "E:/Postgraduate_study/guice/";
        String projectName = "guice";
        String[] commits = {
                "8405f90aa92054802c73fa54c4fb2eede188e2bc",
                "7eddc637b64a8a3322655496617a2786b2683927",
                "bb59fbfe998dc741162fdee658ba8f45b7069c53",
                "86e7ab75b34274ed06afba45dcd24fd72a871f11",
                "e0f12160dd854bf0106ed94d2a36125bad1b577c",
                "65d7d078a8e947bb58a633affffd32ad670e73f1",
                "b2554223ccae1d92b191eb10ba4a4866c88310f0",
                "25d7233fe32821994fd5b30a7e6209a0631c0a61",
                "9a998363f7c74081ab927ff19273e819b11f264b",
                "3fea88408466d0b1037d5ed5cd0c8dc1687aa01b",
                "1e8ede8007e76f7154dae55cbb08211ecb7b293f",
                "6bc51218d9e1d38966ac48f3977c45cf6cfb2f6e",
                "ad77056d77e72b15c8cdda310bf29311163e11d4",
                "3cd3868b658362dba5f99b9a24f603844b93cb6b",
                "909558ec1a12150596ef53779ed2d255d58327a9",
                "415a377e3bb7fe4497c3ee9f573d2652c1651038",
                "155bc423453b77c1b395ee869abd52c8fd472717",
                "2a4df0a72e462d2c842ca8bdeac2078883b3d83c",
                "bcc9fe45e39fa2d743616cf822c7f6dcbbff6e88",
                "c4854d5cf594e2cc03f3a3413d1576180617b843",
                "dea06c1087bf61738fd9e92d81a4c315b2eb5d02",
                "bbd2f0d38415b797afc9445b69112292eab271fd",
                "d88175a45bbd102533e6de272bff75eaceff36b3",
                "240406ceafbef86e26a54049f1e70383a98a0a2b",
                "34eca95f9ef145f08570dcbc1838c58000cb08f8",
                "2aefd5494664a1b847d911e2919f8cfbd4260c12",
                "15c52304906ff5f39b8908b6fdaf0982e8abde17",
                "9d12b481ddd60f8a9cb1be5413a26b76bd9807cf",
                "c6fd1423ce4caa346f915ae7ae46df3d4b01c684",
                "cf759d44c78e8490e3d54df6a27918e0811bbdf9",
                "25fe439254199107116eba235bc5f10c7b47c7c1",
                "ffd9d017a4b6390b5e4c616c6a8dec793da4a777",
                "0477f05101eb93a19554a566997cc34e1a20f044",
                "b007a40f7125dd71a51e7daf5f3d03902b6ee00d",
                "7d7e3da20c3ee3cb038680e5e305f96154da8c70",
                "c1c1a763d00ee50e5e3a459ee058372777950d65",
                "738d317387783de3b2bf357d9417d10156c09b27",
                "350659cc07a601c488c5bda68d4ecf5da4f982ee",
                "e960b66d3d5931b9cb1aebd49e452e2c489a921e",
                "a260458b28b2dad9e2c8cfca2a2783e585784ab6",
                "e6ec2a4a9433d64e763093a826eecf49f005aa5d",
                "dc7f4858f9cbb1babe50e962ed5e145536844ad8",
                "2872237b01cc9d43630ba3b9c483dd10227e2cae",
                "9dc98cb7c710be0f4552e7d40d1cb29553b330cf",
                "6fa83e476a4e8ecc80fac3b361cf8fc3643292a0",
                "96e835f0985ef06bd70a24c77be331c61acd1baa",
                "bfc2e48f4a008462ff74e0e73bc7e1cabc04e30d",
                "df622abf7ceb0984026fabfbcb04d2e6b05f370b",
                "826a97fced733fe8ea60de4ed0837a6f50ed406e",
                "015ed75b5c4a5b04d1adc36ad836cbf8df57cd35",
                "f78f7cfb3b7cb336e8bc8ad7fe421b24805720e9",
                "9a83236e2f9a9814c22dc1b5166368fea8e6227d",
                "2157fa9f12d0725906b0a3b065dc9bd37b85685e",
                "1a299822f02642b5cdc6606430266987d0bb4b24",
                "1a410a8bef1ec721e16f9f16a447235f09951f05",
                "a81072df067e4ab811ab01bda34e4b2722a66988",
                "5ea05c3edef1401f3481685b11f38010f6fe76ed",
                "fdfc81cde4f0dc1811489715a34b3632d9dfacd5",
                "dc5a782524fa9cc0923ba9173050b20c8c9c8311",
                "fd4ef5e6a3dbe570383e1dd6763884dba900ad7c",
                "15ce8eb6695da170fbf3b807515477dde168a7fc",
                "363f568b6de8c093d6669b45d29c804783c9f39e",
                "2712230640393acdd00f70640c44ae739d4103ec",
                "ed6993aa14a4010904ea794ba7623a6f4486fe21",
                "a795fcc1b8eae06fe32463057f60883e16a160f8",
                "70062306dbbe3142b82c2036e8aabb33e0a13578",
                "338d0039c1e30038f22f0d5544842c1e87406a8a",
                "6eaf556c8b1449e97e19a6c4401cedfea0911753",
                "9e7afde6564ef59a707df2c7c3bb38930ab16b07",
                "2de23113188495ce1583acf0f1bef774987c8d35",
                "8b2a048a713a3d2f06ceb39b90649257791dc090",
                "80b91afae2f42b938597387c0e8dce419416ca92",
                "b97095d3881f1e5a8c71b74a4f35d1847d5fab66",
                "d3df2d5854fb55f65a8db44f4ecb4814148e495f",
                "50e7c7e02cd28b3dc5ac99a0e32bc397915f46df",
                "5caa5493958d40f597b4419c5027c61ce13ceaf7",
                "9a8e46716e4c78122308ac8ac23b4e3f8139debb",
                "2c8c7fed126376167126df0da740b3cff0c26469",
                "180ed4ead46db35c3498c17184ca9062b099c1c8",
                "1ae63eb443ec00a4899aae13fa391880db406b6b",
                "0781568a6cc7b3ccf59fa6851e96ab1636e745c2",
                "dd873be40013bffbe07552367326bd2b60eaa807",
                "6c6eb523325ab87008092d986e5b1b1236e26411",
                "36b710d4417801bcd38adb92a16cd6ccc3446a4b",
                "deb5a1b3f006b495385df5f8cb3d065b80797add",
                "3bc61ccec213779b0d5164081825744d1da3dced",
                "b7cadc1cfa0623ad377c274eb8db278e3e9a7054",
                "b0e5ade45e660cd7b923309a4a219d035cbd98a7",
                "690e189a7d6830fb61c10fdc46a8985eac0a7d3a",
                "67c957b5e41507f00e3338492a18b7f697778c83",
                "9997527a93054674aae1f452dcece2bf56b12885",
                "471a25f1c7daf81d376fb1ca558cac731661b347",
                "6586659da6daf3b781226cee4947dfa438c55bc8",
                "fae2e289dfba631ea58f8278637155d0a9fe6a72",
                "0beaff51e0926a20f4d1015224810b61777b481a",
                "9b45ad853618b517edb07551c109056e125d75c2",
                "835084290b7efca75c4dbfdd9c5aec221fa13d33",
                "fb63a55517cf4d73289d4e9ab8dafe20721d80a5",
                "59a07fad81727cf467bdbc9c5236d7d7a471a4d0",
                "7e6a6cf83f26aa053bca3d6fef3a360c62dfedda",
                "ee1c02fc7788d52f2dc094a8b05fcf8318bb3837",
                "a689de7884ea27ff7ffa4e51a510f44348ea2752",
                "77abaf34d69d3e44099848d5c1e1d032211b9130",
                "96319f854c4f6f1ded1f77aba37a40e60cf46435",
                "16e0e9508eec633193026f393d33f53ba8f86b8e",
                "e586f654a95a5c8191e1be3ac6da5a2165d6b5bf",
                "2ebc207348b2c9d41d0c308ccd860f15a08bbad2",
                "de20ce15cd7bd7def59e71aa04fb37728ec0744f",
                "9af6a50bea0ebb5f06bfdc51d037e1e783ca947e",
                "9372d329a24d696eea6a3b126d8e8377e7f7175a",
                "f6325c90e32fc3d2331adc5f6075b6ce465055df",
                "c26e1c0d6de4152e87efdce058df827bab241d73",
                "02d9253846ff3920df555caac3ae76ddff9aa08a",
                "f123130d30cb9f5c828faf1d97dbafe1d7b92662",
                "5fd16c90c1226df1a56c79d3a6f7c3ac58dc9c0d",
                "51ba928a1b641afb2f525b28787aaa5ba3bc29cc",
                "97bed3ffbdfc6b4e338ff9357955f4bb6ddb3ff3",
                "84e0ce89ba6afa8cbe7ded6e7baa49353167a11f",
                "671a36ed86bfcab213928161e03e8c7cc0f92fd4",
                "78a24b86cc98fc86e96d3200dd6e5d132978db72",
                "363c593aa7c115bae39805bb2f233e45692abd87",
                "af26c7247984f7753c1a84ead9186584f7ef8d51",
                "ffb154d0304e7c226e1495e5bf0344ff9313a29c",
                "db66b74249c0c8da50f64f864701ea17eaa3675b",
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
//            System.out.println(method);
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
        Run.initGenerators(); // registers the available parsers
        TreeContext tc = TreeGenerators.getInstance().getTree(filePath); // retrieves and applies the default parser for the file
        Tree t = (Tree) tc.getRoot(); // retrieves the root of the tree
//        System.out.println(t.toTreeString()); // displays the tree in our ad-hoc format
//        System.out.println(TreeIoUtils.toLisp(tc).toString()); // displays the tree in LISP syntax

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

        Tree src = TreeGenerators.getInstance().getTree(srcFile).getRoot(); // retrieves and applies the default parser for the file
        Tree dst = TreeGenerators.getInstance().getTree(dstFile).getRoot(); // retrieves and applies the default parser for the file
        Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
        MappingStore mappings = defaultMatcher.match(src, dst); // computes the mappings between the trees
        EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
        EditScript actions = editScriptGenerator.computeActions(mappings); // computes the edit script
//        for (Action action : actions) {
//            System.out.println(action);
//        }

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
