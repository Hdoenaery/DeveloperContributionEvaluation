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

        String gitDirectory = "E:/Postgraduate_study/commons-cli/";
        String projectName = "commons-cli";
        String[] commits = {
                "27b961f2b40a2d2ba695dc50774bed32401201be",
                "1af9b95aa30c8383588e169bf2fe749c3e9c7c64",
                "62b510f93283fee7225b56c95b6d017b8546367f",
                "cc37692b8edbb78cdee140d5c487e0bb42ec3e05",
                "234062c7d9e7917e2ff01ac3b534e053759c5c6c",
                "82403d07579665a0c194a638425ac24e08581ac2",
                "8dcf11911ab73ddb5455853a31149a00549261b5",
                "69dd16f5348514607101931c982ff2c377dd02bf",
                "2df55d424f80c096d0916a733c8c5292c4b1fdf5",
                "591af95e0a51d067f69f0ddf1a0ac54ef5c04842",
                "c886434a34107af01ae3cf70645e8e7d8aaa9ede",
                "15e12379bea9bb9ce112fcfbab0a0a9c8899845a",
                "f45c4301de8bbcb50227e0663f20c5f0870636d3",
                "9aae655438b46e33213f50e27997be667ec9ff38",
                "ac96d6242fa9c88f7fad3b3a08b8a5f11fa2b979",
                "7b52ba80498be2c019d7b4cba9e1af9bce2b7e2e",
                "44173949e8165537b201d4efd024e31e6e8b06eb",
                "a1222147c2d3a99aa19776ee2dea30974d57571a",
                "cf94289a3ee4c8ac9d9e958f2e8bbc06efd24d48",
                "f87f0b37881e927346af7aa732f4be05ef0c14a4",
                "f2aa3089560eb00cc973b0cb12ead7c3424536bb",
                "caf0b99cfa99392e48e85aa103c815e4e9debfac",
                "bd39a1c9cfb3ee394791f7e015a3045d042c01ba",
                "d63b4bd3e61e83b6dbe7b3d6f81a06967f17e784",
                "c99a79a0d0d7ffa7281c49c270ea6326f08cb0cc",
                "cfd100d631d2c960a2d109fdddb2bf8e9db44c27",
                "47231757c0e1c5b201889e8f3607470321b60e48",
                "7b32c729873802df59b82a8ef7d6db27ceccf221",
                "ad82cd5ebc55bf52647bb419cf9bc0ebd061b3c2",
                "f38c4940fed0cd9abc652f3c539f2306295ae576",
                "20585e0843ded9452b8ecf5fcb3aa74dafd2bd2c",
                "cbb7227c1f19e9b72e9ab65230d3fce570bf9f8b",
                "d78ed6baf1cf85849e2e9ab69115ce73853dc088",
                "9539506e87648d753c44b68b3dfbdd1938c09315",
                "dc9af47ef6149c7895ffcac9ede4d33fb37dfd3b",
                "3d83402d6fa39224ab6650f6a011322e7ac4ab5a",
                "b805e90dd082b4549ef4d565d13c6db8fdb96a5e",
                "81cf67387da844052aaa4c6feb4c8b290308e0eb",
                "98d06d37bc7058bbfb2704c9620669c66e279f4a",
                "5f0dc627e3beeafbe5d528cdd76c7ef21308b726",
                "f78fb5a99093852b0121edf4768bfaf931fa19f9",
                "46131b704f3c5dec42c012ece43703aa8da20cda",
                "e20023d55962858ec6cd3d786686a00192959a9b",
                "353771a4bfc561fde6da57d9d7c070b3727c7259",
                "af54c01799ebf3ed613bfd9cc9e4934676b245fd",
                "480802dcb6fcf351a33dc6f64c9cb5b11e8058be",
                "c67275279176504a6f5fc556cfa247e1df02a580",
                "ef1309b0975a102abcb1d7e351b1ec2438173d2c",
                "1a11c86d750788d512a4c6e5025127e438185fcf",
                "fd66015d8e2e5bbc763c74fe4e8245cba37c01dd",
                "f91ba7058dc9d733d7f8177df88b2f44144be72e",
                "a6f9d7bb96045fdb361d12b91095fd55be261080",
                "47c2a289ecedcdb532a7f393019e3e7f9621499d",
                "bf19d994c6414de253734f69a769e419758e43b0",
                "a3e2d6b14257122a891d84459f927e2417cc8c88",
                "02aba6c78d451376053478e4d627ba6f2fd6ba21",
                "24adba8713785abcc3cbcf40a229432d53b4b521",
                "f79e8335ebd430d7b7a992d938ca16225061cdba",
                "c5536b7f82862fe798ae91cd4b4a8a2df049d06a",
                "18e36386a232ca7e31931bd1d51b91a7eadcf72c",
                "b0024d482050a08efc36c3cabee37c0af0e57a10",
                "04697739edb6b60ecdbcccbfeb4e787728674942",
                "19849509da2a483ba0a8543606a3b570b7ec5c68",
                "23d13f5c3d7fcf810662c31d147c470e4263fe55",
                "3d9587caec7dd23d359bc112c63512e2bc3e0702",
                "4f17a89ad04bcf718aeac43d202f8c261ce0b796",
                "bdb4a09ceaceab7e3d214b1beadb93bd9c911342",
                "9a845a2a30742b500e3b823b105434203427ea93",
                "c17d0ff553fd34e2e53e0446083ee284ee97f6cb",
                "f4a28c0463a414464ebe214a7790fde0b0069e3e",
                "4f9c95bcb246b64f7f6756cc3840d2061a262fe7",
                "1e59d0c2fd1cfee450d0104734307306803a84e0",
                "92f1def0bb3c0345295012e36b7150cfd1d7b6ab",
                "9a048d071f4ebb57179f6ef245dae7769be3441d",
                "89080e24891985d691e271fdd33734e493072bd9",
                "fac33304c67496380cd168d71cac79dbc0e60142",
                "e3d65b0ee3b08599bd787a7721afc326d1bcccf1",
                "c1cbe9dd69f80b627ea046095af842bfafb15803",
                "9039cbd454346276c632ae2424a3c20e18a2d276",
                "abfcc8211f529ab75f3b3edd4a827e484109eb0b",
                "a2afe70f0d14cbf8199b7c8ccda424211dba4843",
                "269eae18a911f792895d0402f5dd4e7913410523",
                "f0677c6c1b967832ef7252373acfa31bb3f12500",
                "dc45fde7e5988908371eb4cccfeeb7a078bf714b",
                "0f56df924fabac851a111a04569f4a3d7331ef8e",
                "ac2a1c85616f0140418de9190389fe7b80296c39",
                "1bf9e6c551b6a2e7d37291673a1ff77c338ce131",
                "bbaf99f10729f95b4a1cb1be8ac56537c7027b32",
                "0304f5946b3f917d0fcb00ff0152ca1ce8e37b3f",
                "7d5129c756ee4de5e7ad7f1c3de81a99368c97b1",
                "5d1d96715b36e32bc27847e84252d2c36ec0cc20",
                "afc13c445a4c80432e52d735685b272fadfeeddf",
                "f6af62367494ff8d0d7844f7f435e5ee36c81dcf",
                "58139aefcacfa7954de1552c2ec5640ddbaf3713",
                "085a1538fa20d8e48faad49eaffd697f024bf1af",
                "f8e2c074559dd66d327c85fbedad58da8b2821d5",
                "206de8c8edac008d279903696bba7c3799bda45b",
                "700f05292777f33b5c349ac2b4f83168fd1ed888",
                "22576c14c60092fb6c67789ab2f5ee14952c0c18",
                "3a730c03fcd3f2715c83db85331d1b5b8c95a1d5",
                "10090594827c3c90b9c0a5c8288cda448b0ce10c",
                "6b87d290954c4594fc69a3a0b85fff12490cfde5",
                "b207a2bcef8e29df436bc393c3569947680e93e6",
                "0c0ce9b1f033319e086204895d3f8fe342ddad90",
                "ce1b4d824ab36e8823819e277153d33c080ada95",
                "78e9b51e45a3b563092f741aa52a7be8f6c538dc",
                "ec363cc653aaaa2c10b19e0ae880ede6084dd420",
                "8a2bc22910f044bf547b7dfbd21b3588e74a592d",
                "8c14cb4b115b3bbb13f9702ddd3c13d66e073817",
                "b1ffe271a5f8a6ff7b6bf714a9b2631e7dc6bbb0",
                "ce5ebdf348d9b1d2e9c45d22c0d9cf582dcd059c",
                "38ab386d9d86c6cacea817954064bb25fba312aa",
                "639e070cfe87ea05f3aa618553e7971ccfaec0b7",
                "faa6455a9a0bccf29d049f0b0958eb9b2e804fc3",
                "f717da18ee109c77f8975e53c21aa6a56e33a982",
                "9ed70efb45a95b0327232d1f9329f777bc428da7",
                "46ad66ae8f1c805b40775e856d70277b669df6fb",
                "df52eee396b1e8ce24fd1ec34752e1049618255a",
                "e89475ca66e263698d3c4f08b589fa7e6aa444e0",
                "08a1eb7875d24830e37a969e9963ccc365586ca1",
                "f83ebd473e0dc4abacad3054405b83f0f696bd56",
                "b2fa954b483fa52e18c07d10ca7ab64849505980",
                "89333a791bf39cd4cc63e9279d9bfa5522800e9f",
                "eae489864a43c79d58585da42078235d3f73b5eb",
                "9d05157309c890b678bd615dce47911350d0dd91",
                "1fcf87d3e9e6e429f6fb17e6768c0562fff6d732",
                "c6cf0e72d4df77417a010c2a2d6d09f69be3cfac",
                "f06a1b95c4568997295e987b9617be799c517845",
                "0cbe33506c92b790fd4dc866237d7e430a6c91dd",
                "4d49ed355c081eb0d8878841e849d9dd7aed2570",
                "1e01bbad08bb961b0e9af1d94e523394803fffdb",
                "e3f81b63a29c821a183eed2fa5169a147580dd78",
                "445ddaaa89bdcd003645b02b600fde1d3730d715",
                "853dabc79acc7c7ebab2a3d1e52788f3526be580",
                "dacc862f13fbe2424c3439dac09ea1aaa42a0e59",
                "57e7048a032802183ad2f017cdd36cc798ab594e",
                "5ff94c7daec3bea08f151df7cfe5b203e4d5be76",
                "6ba1a28a39f42d411689d71bc11932c820cb4265",
                "5b75fcd4dcc69f28fc19d282a0a8a412df6135df",
                "31d92568a294be608bf54efc306e4b9c1e3fe61d",
                "b106dee5f2a22f28c56073aad8afb28efe031190",
                "b445290fbd557afc63827926f65964ef1da7043d",
                "1b1ac53a5590c862f64c39b24d8b22c4e5bb2210",
                "04218b94ea9f1dd71f835bc992b030370f4fdf71",
                "89f274efb8608738aeb71a18bb714f58acdc9cb5",
                "7602e1653c3c16ccb2955da750f6c3f7c539b146",
                "ea85e69e93ba4661412aecb80d22b9de60885307",
                "1e6aa8c501a30c0d92a756fd7dad42244e5b10dd",
                "f481ae3e7a6ec5462800da6349c50e9eaae35eaf",
                "d1690bda07a267c3c64146a8c5219cbe82d4fafa"
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
