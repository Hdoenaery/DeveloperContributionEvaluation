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

        String gitDirectory = "E:/Postgraduate_study/commons-ognl/";
        String projectName = "commons-ognl";
        String[] commits = {
                "6f2e68d054ffae40d6fb68b4d11f0f248b2ef568",
                "f90e61a2b498205ee1cd81c89e87b41746f60108",
                "d9d6b1fc28a72bd8610939017802de90ce72f574",
                "5165bddec796a1e2278c01412e06f44de52c911a",
                "ecabbb231f5628980ba480970c4b298256f215c4",
                "0616d82dc1906a1e203a5ee177f375f74ab62c1a",
                "afc1f9beccc1beee119bee3722efdf876bb77d4b",
                "718f3d2111c5e0ab1649cc43065df33c882e1a7c",
                "b33a4a6ce5ad48964d0e7a7fb9cb013b1062e97d",
                "694464f2f9cf9463626a75b98deebdeddb12c43b",
                "97828f33e10b43893c26f5511e43359addaf8b53",
                "193f06bdc45d67f278073444cd8e763eb6e90a3b",
                "22a46183415828655449bee97c0a25c18f952fc6",
                "272c917af1dc6827c3f585e4a6de25a519c067c4",
                "cb5b58e09def200c5069eeaa5bae1dcf9bf466cb",
                "45fc59324c101d4ab7c7339282f8b23d08c829ca",
                "48f38890712b5b9998c7e88b5f35922ddfaabf20",
                "b93cdf42778a86114512c23f21b271261a38cbe5",
                "e48bc7176be16e3f47fc107f45295e93af3af26f",
                "a988069f02602a32b42e4b14b409136b1496c1a2",
                "925a746106c08a6e66c4b370bdb573f02772284a",
                "83083297e82418cd19f2ddaa7bca68b12798b02a",
                "d819159cc41f01ae26fb0f0671338c1ae4b2c133",
                "8c7d340e93205f87789dceb890ce6d2ce80a304a",
                "2ba85c61358d3521d14560d1d8fc69958f2a769a",
                "9edf9435c2f050a41f7fe3e0823d7d5a2d5c0788",
                "bf9227e6e354d7f931f3f290d33ca24e72e4e8aa",
                "18ef2d24b224e5d0fac26a9a1a1417fa6974a191",
                "c0924cd83e71dfd162cd4995cb4651446b884b80",
                "a0f1684d3dce6b546ff152713a9d220f13e2a2cf",
                "f92202e8fcb28dc39b8099263919baef64dc42fa",
                "97ebcefd77bc596227ab3e50fb58a515d2952031",
                "4503748f194a61fc08cd00c64e83e132fca424d7",
                "238daa39689b72aa55a56f7048abda125be62f31",
                "dfed5be18b3f4c17178a2acb9546c6b3056ed6ef",
                "53817a7a6bd24990082e228f5265d6d8303a3376",
                "fd35fd24cc02d4397a5b9cff94bbfe302c7dd843",
                "882f4fb2b8c0f302e44b52191e768066c677ed50",
                "baba9aec75cc37e01e1881b80869d1097cbfb9c2",
                "c0cbba6d9912f658589c88d0037fe83620fc53c2",
                "85037c981f1cb72d079f141e1b4029eae2584611",
                "3a320a18f118974c4157c41fd65b1ad7140343af",
                "6b2c131799a5a4f7dbf2e8bb6039c1808c082fbb",
                "ac7201b653c5cd24f11d4672a27367cdc9b88d3b",
                "b2a853e855bbffa9157ffdb601950c2209122bad",
                "0e3dd0c37ec33a8c6861bcd6568d6935fd455f7f",
                "f5ca032c350ba1e344110963e8d61e3d1b3d2cb3",
                "a86ab475882b8e74f9439f8555668767c2113453",
                "8298f2b4b606e119ed7bd8e651e17163ba9cd169",
                "01506527ed6b82a1bac6a09607aaada93ddbb1f9",
                "9567483b496dc704203c6ecd4f531640743a74f1",
                "99f0034b03939c6b8d8a88581bc45a94f025dec1",
                "6d9fcdde8cf2690428f344fc7b7051733fe4d12b",
                "fae1584e54fdfe07ce86e381ba9e551c7922aa8c",
                "4341ea8e82bb6974d2d472499d36a888ab62144e",
                "58a1bdec34ba5ee203d6357cdfd558b3216887ea",
                "c77ea1b357dc4d63189be9bbc1466dfed67d6505",
                "b7337789ccd5b01f9d63682627ef4c91e52111e1",
                "9eff1c013a1a970637242e2a948ef813013d5c73",
                "b2a27ab6a9b40e4fb7ceef8605dc06918243af87",
                "a207fb346d09bb2ef96b426566f5e3175d682f93",
                "f2224b5611baf66ec99c72817b22ed4a3261f3d0",
                "259d3770ddac879e7a00d376fcbf1b6e0fa9fdc7",
                "9959e84b4dc4c04851ab34ad6f18e3855a47fc2b",
                "82505eee8b505ed188436dbc6482be282e8960a2",
                "1983524ead2b0d75fa44272e777fc4d0a2cdc509",
                "52184e181383480a74fc6fa09eefbde9563aaa22",
                "64e372b026dd6a8b6cd07013c2264d8eaee3ff3b",
                "14dc11b5db8b858723cdd6e4abf7e44b06396630",
                "1efd4de1977ab38d00067ad01baab0408a37db23",
                "15ec0c0f3587ab44593a40adc4d37af31996f3a6",
                "2f2a22728cb73aaa42be4a5824298ba28df36df4",
                "66ca35f51336985f6dc1006db378787091065ebd",
                "e7ee1a8752a84b64ef613fe7dfc5e3f4a4de5fb5",
                "1b442a436f63e15fc7a661ff018b2fa370521c79",
                "d967479ad53da239adacee02046ea2ba04b79ffc",
                "fc0c4119778602dec5d77b2c57359bef12f244b3",
                "1461364bdfabddffa2a61182d87794e4b18f81a1",
                "25291b1c9791e4439bda4f533ade90e2d3039d34",
                "7158b239d552ea30978cf9cd0facbdce71fe14cc",
                "ac70c1fb86858783b55957545914656bd05692c7",
                "6e276e4e79373b0b3cc3682096e42352bce62a17",
                "84f53f59321de6bee1d1b53c4098eebeb4ab70d2",
                "7dfb8a93b8f8f90852f0d976ae68a4de5238e40d",
                "842dbcc9e070b48491fc320d4f3a6a7d76bc31f3",
                "120b167f292a1f2cdc4f2d83bb3cfe6a7303bb92",
                "c3c74d4d21fe4d44a4823641c686e259a4182533",
                "bc47ab2d49e4435a86156a04897c013c77b03a73",
                "91b1266173f813a590990fbae2faac8c5ef626cf",
                "801d5c080859a3eb8289ed814a7ec0c21ce601a2",
                "f5fa03b31f869f9ba5bf073e137db9186546b064",
                "1a54656d662ed6fc4f46208ad26ed30e6d866bcb",
                "85382a4b5d0d51112cf6ed8103eb8bde0aab37bb",
                "69c9cb6f20946ce6e6819e79f72216341b920f1e",
                "03b9d30d5194deff79c99a00619be5e47743178a",
                "e389358bfec29b8898bc342eed14d790df6d64b7",
                "29e4531036eda4219b0dfef642b3537ae968b63d",
                "b76e694e25372876eeb674af67da0cda7dc4e8dc",
                "b11f01f976c271c621a780daf9c756d305756ba5",
                "7dec9c3cbc22aa36a5fde2c264a9b9267e5c9807",
                "ed3cd30e80eb0b227a95d26d4e7e51efb8fbaafb",
                "2092e79ea20f630c850c43d01d6ab11f7cbd7c1d",
                "0c8f71478500facc5a2ab0f6c9b7a018aae514ef",
                "7e3b9fa64edf9972172620c44529ae7c1cd657b5",
                "eacf8cb21739a0427d36ed798646d4127294d5e9",
                "c8cded028480cbb967cdc9ca5fecf3e197e1cd92",
                "f7a819b5f7778a0a5ab67f755977283d2c9fcc6e",
                "07ccb35108c5766e01d396f46f4b68096d4434c7",
                "c6e5a60f6882eb7708d104b7ab13e7eefca1be71",
                "344416a824b35b34946002837275bc8298486c0e",
                "98ceb4ad39fea549081bb1e7bfcafd947cd74f58",
                "6e7e11921fb79250558d6ce730997d37c26d0ba2",
                "e92134c3992ef955c176291d81051d9eccaae480",
                "418c984f60e6a2d5311205c4d53d8d01fa06ad42",
                "4351f0427b0a26e81b441ccae11f7a6acbfe4ed2",
                "21d3a9f9c38adc8b3787d4b06e82360d0a464228",
                "6eef08fd7e167420bfb01ddfbad7099ff2115b8a",
                "2b8ec98716de498cab89daa35aaa5f68c27cf0cb",
                "18b3031c26e3b1b78f41588a9898450affba2785",
                "b896b768d09dec29d82a1c1ecb1139cb190df0f3",
                "2805d181b64b1af36245c2157cefbfd02ee8b07a",
                "27cc181ec74b9fb1424ea6ccca00a2f2cdea84ff",
                "96796985d6124fe73726e3799c4e6be7243400c7",
                "c184917aa9e9af2e3cd9be1169c7689f1e9b6550",
                "33ed181ea4070b58e793680abb3273ec50b17b7c",
                "31c4731cf20ce3f69e84600b360aaa54ccabfd8d",
                "a53001c55cd3ff914e45223db51c689743ba247c",
                "722d2df2057bb1e362a4458efb914aa590c02c60",
                "98651c3c37628f646ec20d6bc4165f5f20adb85a",
                "b9b4a41048ca4fc4e99ee1ec743c2992eb815733",
                "f7d9c45c35e51fb8277bd7bf92665cf3fac59474",
                "658775e8537313f131b53f847f8d648021981a6c",
                "1adb6c6720af30d2c1f4e5e72dad461ff3815f25",
                "f5c10931d6a270252b920557350684694fed917b",
                "71abec4e3976bb1d8f8a588bf83f2765c7ec1ce0",
                "2442560236f297058f5e4917627b984e19db4537",
                "0a0a9cf4ae4f4ce5866e04bc77af3a927472a3b7",
                "10a393488fac3bea0179b1a0dea61e6a85ad07c3",
                "3e69b2c160eb8160baec3fceef195717da00ee41",
                "d99f167fa2efb79b1d16abc4b9d88e4aa95e653d",
                "5101c38da1fb06daf7e70691116a4c2179beed18",
                "8756b2191df14d57889deafc127ae51e8c9467c7",
                "d5d34ee5f445304ad13633d26fa305c6f227918b",
                "1e30880ec01f16dac119e89c47fb0e3d1a4a0513",
                "6589342e55e9536d348525741f054e5351db57b8",
                "36aac97df37102e460836cc7fe9a89411225f7cc",
                "eb69033b0b365631a700c0fcf47be673e86a149d",
                "6e6ba2cc51f5b64cf075f6dfa991edf098dd28f1",
                "a4b65b0cac7df6c49c9cd428f901abd9af4d04ac",
                "3e0ccea7dbec07c49edf95ce0182b803467df00c",
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
