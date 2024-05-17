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
//        String gitDirectory = "E:/Postgraduate_study/FlappyBird";
//        String projectName = "FlappyBird";
//        traverseAllCommits(gitDirectory, projectName);
        String gitDirectory = "E:/Postgraduate_study/fastjson/";
        String projectName = "fastjson";
        String[] commits = {
                "240edb5c42aa9295bc674c93d25ffe801c13a5c4",
                "679140e0ad6c0bb1cd3b8397f32c5fe55fc7f3b1",
                "16a43f59be6130dd7d8346401e1575a2f1a2e435",
                "7abc84fc2c208f148970ca854f2f6a59466e47ae",
                "19bd016801b2fe99c673479e69bced84ecff83e7",
                "097bff1a792e39f4e0b2807faa53af0e89fbe5e0",
                "d91c05993b17a5aff28a33810fc263402824f508",
                "560782c9ee12120304284ba98b61dc61e30324b3",
                "dd3de5f84bcbf0c13ad60df0c3feb9d564727447",
                "0814909139735ea4c7648e755630fd49da7be059",
                "c04ceb9bc71295fec691f99f7baaee31ba1aece7",
                "aa71e753d8473830da9f5ff9460ebdf4b4e0ee5b",
                "39bc3cb12a982d0225521e6f1d10bd01bd9a2575",
                "6841ca79104f6b78179e44b56ae9a033b8131c70",
                "9130f8e78ed0d8bdf4b56058ae540c50f0b413ce",
                "f33474b4c618ee86cde67d5f589e447409183837",
                "b1b7a681989c5f7fd86d6c618c9fb5e806ccb99f",
                "869746101f6dd73b70d8a9c2b6dc59de4352519e",
                "751291fc5e275d0222ddaf75053fac5be9b487db",
                "8b02d16b8dc49f39a624dffaab0da46ed7ef064e",
                "f10d080d456643bbcb07301e4080ec5291fa85cc",
                "84835dd3cfb46939eb595742ea8d7d74918034bd",
                "b1838616943188618bd9d0323df65858e7e662b1",
                "960fa3152d0b2e983522456937c9d47e8d24a7d9",
                "5271d86fc4a796c14b9713df973cfeec3f583e95",
                "02f5b0afeb2ac71ef7fea5834743566ba9c6947b",
                "bd2531617cd614c71cc768d7fea002813224abb7",
                "25398e4e20efa1a8c7dc2c4947d83c1233083f09",
                "11405d48f8bfebb1568e7a7c32ccfb46399799f0",
                "91ef13b5a1235626c8452f2ce2c6d87a76daf695",
                "4dbbfcfb4c1c06ac6f85e3ad2ed6a2c4b7ba5484",
                "d54d159f209839a5a5ae885d222f1989f6af0ce7",
                "db11d2ade8eb093b71904c970f3cf3067220cb2b",
                "d007be7a5b0890e85d44b4c12da5b3040f203512",
                "697e1521b622c7dca3ab9d74cf9e74b40bd5748a",
                "3dfc699a93462d51dc73e42beb55332c3f71d949",
                "cbd625b77439c772560754f0dba7f8ea7cfa06a1",
                "65fb33c1e3ad1f115c7d992b09c5f6aa5e24a81b",
                "4f1496ba129598ee503fde33e2378b7712469bb9",
                "d6b2f6d7159490804a4e7c18c021d65a5220b996",
                "1901698c910da392bff102db5c07581663bc93e7",
                "2fafaa142c3f9e8ccdad41d22f4eaaf9d5ada76a",
                "4fadfd0ecbde1ac02e598243a0041c49cd349b76",
                "ba07c7fbda7ffb4f5fad24f5d02c03794db8c7c9",
                "aa734544bfba94e6a7df836a181ba4064129c20b",
                "e2f9cc6bf31b8da8e020e2f9c559e7b970c53c7b",
                "8d42fd87c00b9691d393c15998135b5281d895f5",
                "00cdc53606111e7802f8f5559d44feeccba2657f",
                "6070b101a87b27f1bbbce0d97f740eb0956e1cf9",
                "275da29e451773a8abf12dfbb58d81a8b3fa96ae",
                "daec680b7248c2b79fda50b0cee4863a8b228bd4",
                "bab56ae6fe823b7a0e352d10aa355de0ef6dcafa",
                "4d47b4478603e9d2997ae192d8059f5bcd074da3",
                "e59c1be9219e1e8ed8ebd48603b9f5567654a468",
                "3f28f52ebd09d26fc585454d674be7ff54ca7903",
                "ccdfe007c0c0fd7a40d5040f37c6032ae17a8a96",
                "60067f927a1dc51f8ffd53cb1eebb2b18bee044f",
                "911e11861f6f624233266ede36190a5647d3da92",
                "e5514a3a78353178cf19116bd9ef0bea001b5454",
                "8fa7aea250aaee5833b6d5da9ec76cfb63269d21",
                "4370bd29d86ea7c38f93d6712bbfa231fb85f0ca",
                "7b5922561e1b35614b268ef98a99cac8fe5c4fe2",
                "e161fcd62a4a75121bd22773e2cdbf2867a16225",
                "9b8b186772e40a1229c2212bbf40a23e45d1f0f2",
                "03ede75b02f63f481b474539c005cb434f4aeaa8",
                "732c2a33bd0fdae3e6320fadcb52b766bdd91d7b",
                "e6cc01eaa929606ced9d5996192078c9495ddc57",
                "333f11f2cffeb764da7da27d4e83e3b8363427ab",
                "e8ec59ee44d0fe54355c790deb782a35dae9a628",
                "3e2fa459144bf08bdf4f6108f74875e23ff19748",
                "f96732be30922bb4497dfda69ff7acac94c63d4c",
                "b04b950a4a37836c3ac4f801c01e25fe80aa924b",
                "7a793785de55ca41fc6ef50a9dabbd2d5b07b887",
                "838ebd828f76cb13dfead4fbfd3ee478096872ba",
                "2baacfb81092936cd933ee26ed60c0035456308a",
                "62787d9bfa0c7c674ebba69db5cdbeb85e9893d6",
                "1a4fb9bb29188ba8474a20868ff8eda6d9b48317",
                "a38362318bf1bd3bce7db2ada32b325d71bb21b8",
                "be6b5364d1c10a9d37057ee85daf9313eb8a64bb",
                "10a41a2974fe4f9b8dcc261a72b67395833da2ef",
                "b584203cf4d3969245ffc540fc6c51fc5e3b264e",
                "15ac107181ff88781a593e6f9e12ee357d6046d3",
                "28b674e9814902a6bbf472fdf734fcdad1b97df2",
                "2fa8ee07e55b476f778a63633f556bbf6b023ab7",
                "a7f1f5c4ac52503cc95bf1d27498098b3093a0df",
                "8b690ffcc901a9655b5c871795f7be910cef7f0f",
                "90d591ef35c6a599656aaa299d819ae5746322ed",
                "dc6fb0b2a86b5ee914de1932842852c7a63d8872",
                "98c031a740c95edf14d341fb6edcd00d5c9254ff",
                "d342dadc4cbff4156bd1deff03a2e54008ee7008",
                "cd2d5ff1a6b6c19a658f34ace4853328eaf0c500",
                "aa5385637df25ca9dab0051ea57a00b38649d34a",
                "016ddf6dd881850d86ca33b614fc431c4d55bda8",
                "37ed66bcaa3b0c88c5f3f0b09e6b4029c72c14cd",
                "af60554efbd7a0e36f3b646ee94162359655554f",
                "ab13b03d2168be6879cc56b9f1aa12605b140492",
                "13f3f2b674904d53c12f28d13a80c2302377c57a",
                "1f1371e655437855c0d7806b4ce8f304ad772db1",
                "e2ce6bb9233c2a050671962121e94a6a46a15bd5",
                "8b365c0000e3f56067ec9c6de0d6151de269f6e0",
                "133cca0ecaca8de82e47490826aadda38e541929",
                "9baa2d5dac6da26f895db316ef4a2425922642c4",
                "8124e5e8b9c077de6a3dc38b8d7f42b43f692b2b",
                "0ed7edfd25d4c81f86eca0375e457669b6cba1bb",
                "063a3a1b60f14b36bc1ec792afbd184a89c89f0c",
                "8d4ac65ee241fcf26e77f06ef82999b1b5f47c02",
                "4af4b1536d5385b3f37a0c4f467e6b05fb6cb859",
                "aa45d66b189bbd6c1c594b27f331ba119cb9cffa",
                "b9bce375c2361ac33240450c5d18f102d9ac5d4f",
                "face67e357aef1f8534bcaa2836ed321aba60990",
                "bbf9a14583704e9461eec160db8d16e3242e633f",
                "b98af7ade0a0dc6132274a47cf82d6b4aa48c1e0",
                "f602a16f54f6fe4d34b6a5b5f5e7820517fb4cb3",
                "0491ad3c53dcea90191927a7db75d2aba595beb1",
                "84baf138fb59eb9bec5e0ee705ca32b1f3e68f8b",
                "6694734bc70c2acd6b7e0855b3967ff47dbd4882",
                "8e5dad755343fa8aaa09e64056fe6581bf27ef59",
                "789f983b340c1140d2a93b1af6a0df73052c14b6",
                "52dc346746a509719e64322f638a4a7b5077a9b6",
                "803fd738ec9780457f7c4115e452cefa7c36475e",
                "dfe772e8d8f32f4c97c270c7c550d6154dbda50f",
                "dd1cec42ea59cfd953ef159f8173560ee2fb2832",
                "afc024350d49d0f55b92025a4446ec3d6ff7d221",
                "a1891bf11fae7937f3e72064411803511ce18f71",
                "85fdac68505fe5c1d525fe03b7febbfada3618fc",
                "20e16bea10b0e6cc34db73e1ec025b26e422c821",
                "d883f939925af142d117417d128f470d68db700d",
                "5ca2b3bd42add17fe2c4ff56d88ea5cfd457ae30",
                "1a8fa8383d7c5801cde08cbc8a7c023cad422dbb",
                "e907be888e570a5b2db9b1f3c9625a2436a6c49c",
                "01903853d90a4246ef11612a7b8360bb53664b88",
                "3ea25de368b185e3c9f3d56e46a4cfcdb9265318",
                "be0d32b63f2da3f8c97474f05f617d160badce99",
                "590140a30e2e1817448a5aabf1ed814a85588c00",
                "4d0df8ee25ff311e29dd4cc4a5ae64bf1ab093b6",
                "b9105b6782751d9e827b95573f57a157b0b0cacc",
                "124fb5e3669eca2855bd979ebb96d109f3820410",
                "1f2f1d22f60d11aa9ed2b4abbb32e26f425f9f24",
                "c6612329a3250a5e390f5acdd766a4b509fa4027",
                "7d69528e6518deae441f817ae5bc98fc072850c1",
                "8a25fa500dd26aed87cee820e691dfc1b1d73358",
                "b24a2bfeb57ad4eee1195cfc6413e01619864dda",
                "b679307a1c554d8c542b07e52081bfec6ebf6f01",
                "e3dc4bfdbc085b9e3877a19a7b5151bfa314c6d4",
                "37af02346ffdfce9954298ce2386ea7f7b540fab",
                "d1af83c96be9b720e8ee149dac4640dd7b7a07a9",
                "c4234fbbd1b4da059ca7dad0bd17e23394a3c1b4",
                "6ce4b2ad6748e2772e44ea83560fb8aa072f1900",
                "3de9e92f098d2d9b37011ab3616fa28363afdda6",
                "e697d4aad5e3e4b4df9dc7fb6364d312e7239ef0",
                "b86ca3cf1cc9712fc2dd187a98b7f2f1692d9be6",
                "e970545f47185f8feb5ae8e8dd9003b26cdef8c7",
                "f02ecfc668b346823009867ea2b82a1652f4b36d",
                "a45ee59cf71fe346b669548f48a3f2d46fc2797e"
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
