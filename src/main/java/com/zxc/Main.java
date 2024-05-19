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

        String gitDirectory = "E:/Postgraduate_study/gson/";
        String projectName = "gson";
        String[] commits = {
                "96ab171eb48dcea94fd9b8f425f65c531e6c3aad",
                "b1c399fd6296f2e61de37109847c957bab42d736",
                "4dda4ec5ba81a02406f40f3d73192ab9bc568f48",
                "e82637c4856b026f1ff2575c5e3d60c516391b79",
                "b2b1424582f973457e15f4eeba35cd3c0c0ed2b6",
                "b5343ba96ce0e24c77e7be0efdb111f6fdf999ec",
                "7ee3e2787f45f5453b61a9af6c659993b4eba6d5",
                "49ddab9eeb6cbac686711deca6001d40e8d8500d",
                "47dea2eefc6d2816cddb3f30689070285491733c",
                "565b7a198e026596c3fd18304f86537ae20f9a3f",
                "e2e851c9bc692cec68ba7b0cbb002f82b4a229e4",
                "710a76c8b8835e36935c62024001ba6b91ceee01",
                "26e08fe742a9a023cc8eca454de8f84a0223d187",
                "73216b2ad7b16f669000b8d7bb443921ea1a5b55",
                "d38e3974211e89d8e6d74c94178a2db102c0748d",
                "6b96a389cc40d56fd3d00547c00a42ab2bef6098",
                "dc28951fa7e7112f0e229e0a4665ad27a1891d79",
                "615c8835d309e1be512dd98809b48332ce70250d",
                "6ffcdf302939ad405abdfb468218b8caafc46e9c",
                "eaf9a0342d69b4cbbfb3644ce42e196453ce164a",
                "16b42ff5805074126c2e5484450c182773e408a2",
                "0313de8206ca6f68b31c9c01978ec9899677649e",
                "6e06bf0d89ad71f317c920cdaf9981a0508446d3",
                "b0595c595bd2c052cd05e0283bb37b67c02bd06f",
                "ca2ed748ba6e31c4a319ea6f2d2dc7048021f0a0",
                "cc505e1b9fa6e6e29466914a348315f73d326d27",
                "a92bbf849c99b1fc708eb3dd209af9c72110ed4d",
                "deaa3a6cd9f4676e0c826eadadd2f3d6dc857096",
                "e0de45ff69ba3daacc3b7623cc74fc69a4eaf6d0",
                "b4dab86b105c85e6b7d7106c9ff11e3e923e3485",
                "b3188c113205bb41a980b09917b7f6b242cd32fc",
                "ba96d53bad35f7466073f14cb3d89d09383e1a2d",
                "ca1df7f7e09f6b1a763882029dd7057f475b31de",
                "c54caf308c3f7d4a6088cf3085c2caa9617e0458",
                "e6fae590cf2a758c47cd5a17f9bf3780ce62c986",
                "bda2e3d16af776e0f607d56bbab6eac22f8f2d58",
                "cd748df7122ea4260d35dfe90cfab0c079a1504d",
                "fe30b85224316cabf19f5dd3223843437c297802",
                "aa5554e69a2d5ee7e557b3cc1f5d68275fd4c1ab",
                "26a1928277f7eba70609f02697509ba9258dd8ef",
                "ebe4b581ddc5adc0e3cab0d3a7f214691ecebf24",
                "4bb67483f926a5a49d173e2fef1be5cfe58f1ea3",
                "c8f26dc907515b40dd2ddb471ee1d6cc097d0e8b",
                "ac14b4c197382810601574f1defd995842d7bfd7",
                "62a97023852f879eb5819c3ef0fb7e7fdda51bc7",
                "59a8aedb37ae35d4d2a4306c92efd463fffaaf69",
                "20720d6a400eaea63c44f088479daea426e2de99",
                "6ac9f7d8400851fa3d0a136817e880126de9840b",
                "94f894cf44bb908c1dc9b5d7f0a10185a80dc7f8",
                "68f99f2440e93b1f80b241ea575929ffa79b9513",
                "425cb25549ae83082b5e1ba4dfbc3bb635a15faf",
                "2d1981d39bfcadfeac553582494abaec2fc5d737",
                "55115a5ca259787ce8d4deb7952d198f50591f92",
                "b1edb7048687c2822b4fa006ddeb5e2cc2a038f9",
                "b39494dbe68f91045850778cac4b661b38beb615",
                "a4a235e14a63b9a4f902805641f1160c6bf5aa93",
                "4fb215c9df408ef48bad1288ef23c68b7910a7a3",
                "380c4ec12c7a8b2b85cc3a8ac78b3f123162c70c",
                "361292f1c192cdf06195cfd5346763ba38e3ce0d",
                "541252a9fb9837eb8867c094adcd37af2a655731",
                "9171715a880bf65bb698c6c76480d0a3fe65ae20",
                "55acc23d8656a9fe5b19b92029a2dc7277e3765c",
                "fa947212e010e5757864fbebe9cdafde8faadabb",
                "4d735f190336ddf241eee48fa8495eabf637a330",
                "530cb7447089ccc12dc2009c17f468ddf2cd61ca",
                "47b28d99a17d492b37fcdd0fc41290419a010a0f",
                "9bf25c2eb6b6211550f7ba354641dba4bb887b35",
                "21fc362a7ec9d312c34ad82df6188c6480280021",
                "7845c380778889eab5c57c24da416bf4f3da9b50",
                "3958b1f78dc2b12da3ffd7426d3fc90550d46758",
                "aa236ec38d39f434c1641aeaef9241aec18affde",
                "63ee47cb642c8018e5cddd639aa2be143220ad4b",
                "c5a3f21fbad3828deba1deb88168e1f76829f807",
                "477b3f77e159d15531fd30f41688d4b732a8b042",
                "46d0f58d1266c17a9fe6354344e9b1b7145fcb3d",
                "3ee3c150edcfd39b8e72b49e33b2028938e27d0e",
                "5370b076e329444860b0ea0c7f33b10504281f27",
                "8bf2949c2934cf9a168df24d0d2a29c702556a91",
                "a817604a417349af15df0daf1589319282565f22",
                "bac26b8e429150d4cbf807e8692f207b7ce7d40d",
                "b75e1bbc7915f409ba0254fcc92c71ed6798e69b",
                "4d942db168c593ba86e46e2b26b026ff2b0d1018",
                "5bbc768fa6cd3e8a3a7bebf52b0ac1e4c6e0bd12",
                "69f7c4e243c385b318ed63205817347e4bbe379e",
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
