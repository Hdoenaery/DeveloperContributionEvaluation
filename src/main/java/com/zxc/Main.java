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

        String gitDirectory = "E:/Postgraduate_study/httpcomponents-client/";
        String projectName = "httpcomponents-client";
        String[] commits = {
                "944e308a52e985e2498287a82f71dd4e03fc1724",
                "3bd017cb0a772f0c33d22dc1f8fd40012d33dab9",
                "91a93accd70fc8ef91a68b6dbfe6d211e7679719",
                "9e0ff508e82fcdf8b8e6edfe5885dae06d48f638",
                "26dcc6f914999aa3b07ede45e0717ad2e60053a0",
                "db47570efefe32054f6d5a45cf0023e9d6927338",
                "34327ae83e0055a4ebc354677f446c23d793e119",
                "daac18619a5fea8a68a0eff17037a46d364a8d43",
                "62fb4bcbe09c5fb002dcebe408c7bd1d1c0990f4",
                "36678c44dc0e43906908f68bc4ffbb5ae615f40d",
                "94017237b2046574267f46bf6f441bc560378b58",
                "c395aad5adb4e9ee312559937c08382ea8215e7c",
                "f00ce5da9ef8b0de42717e62ccdbf30765e25af3",
                "a3bbcc82ae9ad126ae1d0cb7b0839c288ed05c70",
                "5f9bc347ee6a2191b2ad10eb2e0ed4c19d35f83d",
                "6a487ba686bafed9c642069e6046337af1bfd3a6",
                "23bb9b89deb6d09a29dd87ac9cb0ed7b864ae910",
                "04aeaa5bcdd63d6a21ea8ce64e27066da689683c",
                "157174543fb28cf701937e4a377fc851ad3e0f3a",
                "19626731c0016f6a0dccaabad8ff2eac00c416a3",
                "b9a6b5ed897c0a4cafc52d96dd88c6eceac3797f",
                "8881ef4b3fe80245ce0057eb7d6863ec4265ff71",
                "235900eb57e3a7cdf84021858062025aab861338",
                "75e8dc6f9bf8b2e80924bf6d0420790c20ad97fa",
                "d4c0e961ab69a8e170534b290bfd9276dcdf0078",
                "3ee994b25c9974f9de16c71506b15ea1991febf5",
                "9e876e7ff004608bc74a9b723195119123ee6625",
                "d2a9977290d05868c94ee027cfa3ef472b159dcd",
                "dd0bbda07091d1f5532cf998ed91c90751049d33",
                "e6ad081b3c1612a538f0108813cd7c10870d26f9",
                "19571aa20755f14984410aa0b3f1a1b3b4c146ec",
                "6ba9b4acdcba411a035503e3eae9e17b64bcc59b",
                "4c464b2432525102ad293c20e636970176b74c28",
                "c091c05b72ee2f773af1623da29638a797813459",
                "f8f5bbda872e425dee4df07234f2d4e3e52f3b66",
                "d323e0d68475e6b04df59bc268df832480e2dc4a",
                "0a42d173ef7ae4497439cf52d75d43ef51e46541",
                "ef9f8ba9ab13bb2f67163199a309ee223e012949",
                "d2c59fd5e40409248b4c9eb876d67249ac881a1d",
                "af2cc82e823d696d874b561fa3d81c754b6fa04d",
                "f055b3e83bffa4401fcbec9a19f694f84ec760ea",
                "e09c5d0691a23ef06e36bae6986c8fe373580eb7",
                "4d0caa4f423aadfb0fabc87c11a9962f5c7e4d7a",
                "0e8adf79dcc82e8ddeb24f5c9988138619845c03",
                "3dd37952bce9cc9bef78e248958f628e21d96b75",
                "58386f857bf09f320aea4baa6715e1304bdfc1d0",
                "0289c78e8df1827c9fe04aca15d098e876abfdfe",
                "000fa7bc102fc44272b8c89ac63a43453b62a560",
                "1fb79b96f6b79d8146333c4e21f187b74f06d698",
                "dfc2086d2416af22eb97fc1a601aaadb486a5378",
                "ffc8cd7585f13689170bae1e1b894f649b6581a7",
                "a02455acb31ad7e676130d46d6a04bd97b76e957",
                "fff097615bea52776a9bac3df1751d1965ddea9a",
                "277c7228c3c626b93b298dc94aee4a7895851715",
                "012429391ca318076a9dde8e9053653d9a3c9af6",
                "bc7aae743ac7705f58af0ea9943ad464cb8e70d7",
                "238401731bbc53dc1aac23a58447b199ed4c5db5",
                "8b73f6b83ca85193662b26204cf57d8d025a8465",
                "b10d43f2bb810913c421eb1ad6fe08b277692750",
                "445d4271f9bc9061e2c98cd25a5f020fc4ff05e3",
                "b6ae693fe5a86ba9a8390bd209a71a32a1922226",
                "4dd7cefbde157b76baf245fb71fdbf607ee5c5b4",
                "fb0c0737836a73eb162b564d72d13084008ff724",
                "30c253b37b3a2687c33196d4bfb48fcb865ffcd8",
                "5390aef223a2c4e4f79bf98a8b592066e2afc811",
                "013851d898510de3ecea401c4548fa17c99b746e",
                "4150ac0592e29b885dce51d602cdddec853b4c13",
                "670f0456ba4f5067a1129b7c8c0e1dbfc1d3d52b",
                "88a05247e16508df50168c47a773f8e61fba22ee",
                "7b47b28d469808c99c89fcaacd78d44ae6b9ac6e",
                "17ebfc529e450b81e87c7fa9c301f746eca93054",
                "9496bb8475792ffd1cf695c26c0671df3640b9d2",
                "90f69c87b27b721ea8f0e23bdb4baf92bd7cde06",
                "4ce032c92c6f1f7beaafbb5622647de8db93586f",
                "50f93ec18be8d6f49138825356051c4c0b60dce4",
                "92f757eee3e9f8f964b3cdbe6e878daf75e989f6",
                "bdc7f3b93e36a1601240dc65dcde526e76dd4ff1",
                "c56c00c54935aca35ce3cf3e97fd6db3f6463509",
                "1eeca062f29f4fe6d63f9c4c59528891c40ec9cb",
                "755b69ea3cc44557ed0517b7348ab16870a39975",
                "5164a4e7b4e122bd3f81db212f6d8ed4a681c583",
                "29ba623ebeec67cd6e8d940b2fed9151c16e4daa",
                "aff1d2024c67a96b40e4f8b4c33d65af31cb6bb5",
                "760795b6dfa6db8359aa9a4cd740a09ffb49b8f8",
                "d77112f608ed188a899acb2b9940118b19568768",
                "fde3fca687e0e5aab5d67242f2523c112e623451",
                "0805cfe582b377efdd48be1dc2e7af49d17a072f",
                "82432f50d9c068d3bf71476915f757017f366ce4",
                "8580d7fddfeede3e6dc0d2c68d767b62fb23b385",
                "207265198395e4267200a200ab8acfc37aa78498",
                "f36637dc2f2905e52df76b3e95e71a9732a06982",
                "f5d3c14afdb3a00ec4eb32ff2771f0a503171208",
                "bb04d078ad9672027efa02a4e16475e6bfd8ee0f",
                "c4b2a8480df9899b432e2e7a38836b77008b394b",
                "b151df7e8ccd5dad31193eb8ad7a7f34e14efb07",
                "d8d7ad37a31a3aaf30eea5c08c1109aa5b3ca316",
                "09f50cd80c9cabc9f7532b6063fe67695f5484fa",
                "13137eb6c7071d9753121bd3013a8ba273abbd48",
                "656d0dd4f3b5ceefc649cb2bd0d952db65dd014c",
                "58a17cc549a60b54d78edf6b4a25e92ad948c0da",
                "8f31e6339de0bfdc282c1d2818c0befb3d309a7f",
                "935abae04e76f5de3971ad60b80dfc5d9985d8ad",
                "f5975881983f63560816b84038822751b9bb28e2",
                "e0c049060b45dcb9b93b7802edee3e5e4301cae5",
                "73c1530b3f99f8c06d73adb122bc9dd24279e27b",
                "bde58d6addd4d693aa5aedfafc1406e9952ff22b",
                "879a063b570bb9dd15022db01c855dc71304b0f8",
                "10e2deb21007279720bd93c8c10c5af1b987b951",
                "5875ca1c37e2cf9e1d283158e7ecc3b0d03afe70",
                "2404540f1f1963ab20c1edb06c2fc64ee377532c",
                "e0c19c0b539b332ee3fe73e871463c6e19f9e16f",
                "567b53d4b1130a2522970bb76f6c5eef8555435c",
                "1174c240e29ca4f5e0609583545e3e98b3a91263",
                "d94495131ab2411aa701ab5b41fb7b3a6e40a04d",
                "118e7359a15cc8664a0dc0a9503f7055404df74f",
                "d41d67fa942818cadac5fe5c984eeb70d1f0b574",
                "b6b89a7296cf5017b94e8996fd519c13764f1907",
                "8b29c0680bad805c2c7b529e186b7ffba3cac87f",
                "3de88293fe665e67df1854152faaaa10d6b23ce3",
                "8aea7da1dfdef6b2bc9db50c8c39c476f5ef42d3",
                "de5c6a237a7af88d7f3e127f8c9e41e8e38db7f6",
                "a0184188c1a7651e4fdd8d4bd899506be0927e30",
                "c39117e366378c6e248329464707a0b115638860",
                "f2e9ad3b117ef5b1b250d8f1e121f67899045e0e",
                "4b295dff15d1eac091e6129b3a13f343885e7d0e",
                "e6a7fe8a0cc6c1f4324b6078435ab6578746f6cf",
                "af3a7526d2beb4baf7fe28c992e5974738496642",
                "0940d35602f505a9c0026ea7ef353971af5e4ab8",
                "3c9f1f85e1a17338281f97f153ca6b1804b2d664",
                "6a02e818fff4c71ea11945e45f6e42aeb1e12a33",
                "8285223560d9c83c6b7d3a3f00dd972fe26f928f",
                "a0b4dbb34bce311e894663227d0fae93e33b73f4",
                "36e1bde6ff77a08a0a3e01714ae3583f2283a2d8",
                "25c124917bb3ea83c2ff00a6c076e12b260d455a",
                "8d5cfd326e4ef0970f1c25c34ad6c8f1299edcdb",
                "22cf9671b139be6b49fceda7f43b2a9476fbd67c",
                "871ee6bde7c47f0967a133c5ece867d679dddafa",
                "75f9adea87cb5310229e8e2c0db15161343bf11c",
                "712148ecc3cc088e1d674dd8ffaf45845b3d2e95",
                "956b8194ff792d07fbcf01853f18126dcf9e8264",
                "400771a1a7caa61affcdad89a6b5f96fff8652a7",
                "b7f851104afea252f910ee8ec39c8005be102df5",
                "460abd7474e1a700f387298dedfb9fc217388389",
                "bf1822c55e2729623ac5dd8e758fb0df4d9f5026",
                "f6333a5001450ec68b47bdb019e78b97a74f25f2",
                "c9489606d5ae6ff3af6eac9f407260b3e1f4dca0",
                "feb0377476eb6354288b969648e57414ca1deab0",
                "65c6c250708b409bc4eca8a16a9e5e8bd9870ffc",
                "1026a1e55880f59a2dafb654347cc120f234ceaf",
                "e73b33c18bff8daf701b115b56b5d6557131c58d",
                "a2df6f1e325fc528e02cdb6a099209edb10620b2",
                "f9b46db6e0d7bbd753bce13e90f70ecf12713e45",
                "12a6579513ffc9f36ef41a8d18fc456ab8ccf7e8",
                "918ac1535f37c47a2230f14b6404d411ee7df91e",
                "6395fa7c79b62f99d0c114702dc0bcc8a5ef97ad",
                "0524eed4b9272c3e88447f1442db90a85d772b49",
                "ee26e23b31011b641b1bbc646fe38d42d0e72d00",
                "ee5437c1f1467474e3f232ff25355feba73496ec",
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

    //生成两个commit之间有变化的.java文件之间的编辑脚本
    public static void getEditScriptsBetweenCommits(String gitDirectory, String newCommit, String oldCommit, List<ChangedFile> changedJavaFiles) throws Exception {
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

            // 获取最后一个斜杠的索引
            int lastSlashIndex = fileNameLong.lastIndexOf("/");
            String fileNameShort = fileNameLong.substring(lastSlashIndex + 1); //从文件路径中提取出文件名

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
            System.out.println(fileNameLong);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
