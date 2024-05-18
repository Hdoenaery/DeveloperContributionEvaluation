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

        String gitDirectory = "E:/Postgraduate_study/commons-exec/";
        String projectName = "commons-exec";
        String[] commits = {
                "8186ee338f01a1fd90028de88638032cc2dbea64",
                "ac9f449b792169ff51b42f9825287c1c7be5bceb",
                "155ef4e5341628c4d37595aaf67aff6bccb7e590",
                "3ae449bc88c09a35693231ab0c42b03f9c02cae9",
                "a374f356283a968897ef9ca3169c574108fa611e",
                "bda1a3f664f7feeeac5d1ea2348ccc6c51293b0d",
                "ce7da84482ae133e80d7051d13c21f9ce32ae856",
                "c01ee9a87463682eab74186bed3bc428299f4567",
                "bbff53160f71735df6394c24989926d31abec1a4",
                "611118338bd16ea706cc126beacf2ff0e94f3188",
                "9288fcc7084bd492e550d130f566bfcfca9322cc",
                "3195a506faebefcb114a74c8de1a6c9b468c6acf",
                "0ee055d21c080f9303c13aa2a018a0748ff0d1fd",
                "c8d0e571a700e2d7bf0c1afd6b488b9729b8facf",
                "15ba0e88661a466e8fc8555549a12096e9093f42",
                "bc2bf739f40b3ee8dccc49cb82e6f436f9eae77d",
                "d36f9876740c487e7ef699c405376495a336a510",
                "c1040ef92e2caf30234b9f0734798cf707b4904c",
                "af3b0c70ef61af13c23f9f0f0bf4bba497d0bf65",
                "600caf446b97e62250499a9ab76bed231a80d00e",
                "b986b3135cde13fb43e5c8fab0be670dd459b3a0",
                "0c8c2ea5f2b13e495f60c94b0f0dda8eaa686763",
                "a5a98e4427efb623631df688719123bffd24cb44",
                "db89b3d66c21c1181ce3afaf049a55e8271a9303",
                "15bacae628b97b0c3dc785f76b0fd2601a03a9d8",
                "c8a5398567cd44fba754aa823e48ec0773bcf03b",
                "f05948f438d339ea563b69a0c37136d674065ce7",
                "d8ed2f474751fc46c3599beea131fc1587dfb216",
                "4693924121612b792866b8b90d9cadb2e6d35814",
                "f953c5950ef50a7e88499d2fe00adf55cf92fb63",
                "2e48a4deb15f4e67c34742c5736623a85f3fbd5a",
                "e81c24c86f7998602211a138e721e97c78f63ac6",
                "fe777fbdcd0edc722d5565fdf60b0c50250bad66",
                "63de2d9e70f22747a97b0ad291838a85016bf5f5",
                "cef8deff384d3ba46649e8171b8d39a928c13373",
                "08a223ab8f79b340dbfb1579d932c9c35bcaa24b",
                "c13f29d3618b73b523cc99abe1c14d69d4d2d907",
                "d6ebe7c35096ba9c75f993b7e731f79f035ba8c6",
                "058dc96ffd9f7e7485e44359997d076653cf195d",
                "42eb77c321f7dc17aaff45e45ac1179256517005",
                "fea91e31bef4b3d0a7e52bfca7ccfdedb5b54cc9",
                "f0fce6e86866e23d33ee22fd826244c005537425",
                "54cbf6db7022d66146c2dac8fc38cd7673efd796",
                "56d1ae45038dc4cb1b6b342153db975a00c3a297",
                "2f23f937caed2d721cf0efa7e58ef3d95e3dc554",
                "66352c90db74be60cc4419df7bd3b3b7fe9f26c3",
                "33365fd2069bbda9fe3f51d64b360fd09495c489",
                "a346cb0623738c62b8bec7b00ec46524c6cf1f71",
                "b539675db2dee277830c748434edfeb5b3b482a3",
                "5e2222f14fb7426a10e5e7382e270e85fec687f9",
                "360c83b02a34e98c9f406a4a3f4618e3e9327c45",
                "0d8882e434a8f11ceefc6f4a697164c14fa04a09",
                "8a87f462736eabe0ac516ebf0ab0ef06aa22f49f",
                "0bf316211d17fc0694511baa202704fc3a204c02",
                "d4175bf41bfbd706aa298433d8fe3e2fa22cc8d2",
                "c3e4d1acb1d068e31f874611dd7210a78ad248ba",
                "ae3f40da41ef92ccfd8a39017385d3ef9ba106cf",
                "f1dea18e64fdc668ff51728377452cca93f8356c",
                "770c4709fc729591b6de5cbb2c4981fbaa6b0622",
                "d987abc9515458ccd6da3a279ef8fd4b0f9dd23a",
                "2f903e46dfd4d78f54f11774b9564bde49938d3e",
                "74b2ea29d868516096fc1689994ac9e676001596",
                "245b4efe2aba5733744bc3e447513508c683201e",
                "f30d0e2a203edaa8081809441868acfe20b267c6",
                "f3a41036e53ce08c5b3db9db8924f09db3c57921",
                "14624a89d0025832e73fb85ac96350e2e3488fa7",
                "6a533597683f2c2c11fdc4905e120528f38633fa",
                "0128c4effcc98098ba79a6042dc19ef82164cdf2",
                "2bc62f36d56f8e38b816b35bd7f2ce312a808b30",
                "5948129e05ba9c662d7d8faeed5f28a546f1c1eb",
                "311575dc5bab58b063819a067cd8ec89b4ccde09",
                "9a80ee239555f5011ddcabf4e5e3f994673997cc",
                "79d401e75026ab628251647f7cc903e3aa9540de",
                "13ee3e14272130646bf71f351f3c4211b5266f3a",
                "2e128089b94aa7482c61fb0b49f072d71701f129",
                "e938f2d6bd52198e87a46758aa581cb582433b17",
                "09b05ff1a1f89d45f5e464c663c9a6557b08b69c",
                "c5047a803449a38d042418543f8c88611617f9b3",
                "a8a42c63a7264cea1ea2261e35c394379e002455",
                "068ea6ab2a7b81b5db5d4d6fea88e58c9323ebc7",
                "1b86b8590da4a8470d9602f23e68d6da1e374606",
                "385b1179915c58bdae7dfdf45b2a95eecbb58ddb",
                "43608fd74fdc33dd31c6a4a2fd38709279781a56",
                "8b3a820b91b42e6504d12db400382a7f9be4d478",
                "feb0fac92709bea7c0e59f2603ad2fe1c9d21051",
                "086c2c24eab277e005d7dd7808c04d9811464636",
                "69c008475b91914139cc19514a91a1671d9d2411",
                "a9dc0cc5ebf95748703eb879ae7ade0704b868b3",
                "d4923c566c9db6254980fdf96b5241e16ff1a74a",
                "666f3b938b51ebcd10b84e7ad671af52b6a22774",
                "1f1bf732df4c2ac5b25c75bc3a422ea6958df36a",
                "01bfe91bbefe14424f2da493b8d8da04b4b1ed6b",
                "201535b8331eb639f1296c39efbdb4c1653a957b",
                "16ca2cb6affb487fc01dc1fe5a80b5763f017a7e",
                "eb3c3aa53bb20274cafee8e3eac6304100e4cdea",
                "99f5cd187a522c2cf41c49360f16c2371157e29b",
                "c796c314bf36cb93d8cd1290de1419b28a2c4d76",
                "4b5cd38ba240fddce20d06d346be32057ba4c0a4",
                "ef411a9af10e9f4d210181b6affe27564faf3aff",
                "5166edc0fe7923d607b6714338a02e57932e67da",
                "0cce52216268fae91a3a0068f6836971377b778e",
                "6e795c4840cf8ed7adfc8635b8975eb3f0c032d9",
                "af1c7dacf8e920265bf44c6b115b52fc5916ba0e",
                "86de7982d909cf134e7dc30015faf96b7f509c77",
                "c266aa4872419938d97f850728d3edaa35d75a39",
                "3aba80ac1e3773b4a616d53c999b9daa06a63f56",
                "4f20b724dc78f669207dfc3b22836153cff3a029",
                "2d1c446455d547d63616ed5537e3d367b7155537",
                "d5ecbd3f0af26a7f599ff7a29a9eeedfb5d8b803",
                "632639961b06dc9551cca1f77e622a561ed5ada9",
                "61085207bf5f5872c6cbe60e31ebd3ed7db47957",
                "c83ac01891ce509f1ebc67d236c4f2a16877ebe0",
                "aa2d2ef33e61e28aaa36df946c5cb6d3d75ec827",
                "633347c8ece8f843268d55362fda0c70b1a319ac",
                "3fa782abe615af751c5dbbd70a69a13361484ad4",
                "19d13d1a7927cdcd3fdf8b3615eb78541e40261e",
                "b8eecb8584b0dff79cf04068640d36136f4cfd0f",
                "7f012b5d13f07de2e5a51b266498e74c00bab6a3",
                "0c503291008ebdebcbc28b176cc6c62ef237e2d9",
                "88f107ca328afcd14a8a849dbc44adcbdab2f607",
                "351ae2e174d39fd7f9dc9f1d3b608bae2fbb60ca",
                "7ef5077a347d016a80982ebbd71b6473fc639266",
                "024ea33548d69b53b541d7738102c15dc79cb3b8",
                "b84022e5ae1a681ef90420b3d0827d12edafda74",
                "c2c6c248163bde6dbd5d7ea5879deb709dc16629",
                "13047e98381d965f26b23a5f047314a5cad9d4b4",
                "b941cab2bce9894f6fc7cfb6efaf08a3cde5fe40",
                "cbf5068bcd3886a1ac42e571e2c5a07bc2f0ca0f",
                "fa5d67424430acd09695a4c88247a547796c857b",
                "91020b9c4ea6269d38a1234c12e557eb9cebe4fc",
                "b738fa1f7661cc5287798b25904405e42ba8e54c",
                "292ca67a146587fa5fcaffad9a6b2fffc4f42be4",
                "c9f6980bc2f4d19dc1d5577e2fd6272573cbbe42",
                "65c0e440a4e4e483bcfec1309acd257e3076cbb9",
                "b6d626abc5df64cc51119e06a41b874723112418",
                "e33d951e10bdd859a8274ba46f47368b3fc42ae9",
                "4ea8c97ba35be092902ad974f1bba2168ee532d8",
                "76b6eb88ebd1db3413dde6105a420c1e2d7bba84",
                "eb5bfa822a48d2e9d97dc26e1739a7ab2aaf4878",
                "6df7a6041bb7e6f626fbb19d3b8a5b52237aef7d",
                "810be930a9a3b3f1a90c190c527fc551e8b0d8ab",
                "69d23e8acf176d77ff57b30f76c4ff54fdf00929",
                "5f3a6a6afee65ae851ff54627c29876aeeb426bb",
                "5275290f7d3474c1012808b0f8ea1d4c2b473dc4",
                "5e11af0cf7b819dd5b0b95d92470a6adceb689e8",
                "8b4791561f57b7cafbd1866f21a1ed46f00afce7",
                "3360d939a5d1f3814a37c12c1a80ddd9cc7f0e43",
                "b8940d020ff3fe7dfce63718b10fa7071fef8da8",
                "c56d48c7aba21474ea59ad8e6db6276f6188ec94",
                "5c7429be498b15d3077034998cec92610a695e1f",
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

        getEditScriptsBetweenCommits(gitDirectory, newCommit, oldCommit, changedJavaFiles); //获取两个commit之间所有发生更改的.java文件的编辑脚本以及每个文件的AST
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
        callGraph.getCallGraph(analyzedDirectory, outputFormat, granularity, projectName, newCommit); //获取该项目的调用图
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
        ddg.getDDG("E:/IDEA/maven-project/DeveloperContributionEvaluation/changedFilesContent/" + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/", newCommit);
        // 记录获取DDG结束时间
        long DDGendTime = System.currentTimeMillis();
        double totalDDGTime = (DDGendTime - DDGstartTime) / 1000.0;
        System.out.println("本次getDDG计算的运行时间（秒）：" + totalDDGTime);

        //记录获取CDG的开始时间
        long CDGstartTime = System.currentTimeMillis();
        cdg.getCDG("E:/IDEA/maven-project/DeveloperContributionEvaluation/changedFilesContent/" + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/", newCommit);
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
