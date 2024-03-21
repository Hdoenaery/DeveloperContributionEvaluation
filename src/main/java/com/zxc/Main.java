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
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.DiffConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String args[]) throws Exception {
        String gitDirectory = "E:/Postgraduate_study/FlappyBird";

        List<String> commits = getAllCommitHashes(gitDirectory);//传入 Git 项目的目录路径，获取该项目所有的commit版本


//        String file1 = "E:/IDEA/maven-project/DeveloperContributionEvaluation/tempFile/8f0bd9c_to_01b2479/old_JSON.java";
//        String file2 = "E:/IDEA/maven-project/DeveloperContributionEvaluation/tempFile/8f0bd9c_to_01b2479/new_JSON.java";
//        getASTFromFile(file1);
//        computeEditScript(file1, file2, "", "");
//        gumtreeSpoonASTDiff(file, file2);
//        readFile();

        String newCommit = commits.get(commits.size()-3);
        String oldCommit = commits.get(commits.size()-2);

//        System.out.println("oldCommit = " + oldCommit);
//        System.out.println("newCommit = " + newCommit);
//        String newCommit = "e161fcd62a4a75121bd22773e2cdbf2867a16225";
//        String oldCommit = "8fa7aea250aaee5833b6d5da9ec76cfb63269d21";
//        String newCommit = "679140e0ad6c0bb1cd3b8397f32c5fe55fc7f3b1";//新
//        String oldCommit = "16a43f59be6130dd7d8346401e1575a2f1a2e435";//旧

//        getEditScriptsBetweenCommits(gitDirectory, newCommit, oldCommit); //获取两个commit之间所有发生更改的.java文件的编辑脚本
//        String folderPath = "DeveloperContributionEvaluation/editScripts/" + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/"; //这些编辑脚本的保存路径
//        ASTScoreCalculator astScoreCalculator = new ASTScoreCalculator();
//        double score = astScoreCalculator.calculateTotalASTScore(folderPath); //计算这些编辑脚本的总得分
//        System.out.println("Total AST Score: " + String.format("%.2f", score));

//        以上计算AST编辑脚本分数
//        ------------------------------------------------------------------------------------------------------------------------------------

        CallGraph callGraph = new CallGraph();
        String analyzedDirectory = "E:\\Postgraduate_study\\FlappyBird\\src\\main\\java\\com\\kingyu\\flappybird";
        callGraph.getCallGraph(analyzedDirectory); //获取该项目的调用图

        int lastSlashIndex = analyzedDirectory.lastIndexOf("\\");
        String callGraphName = analyzedDirectory.substring(lastSlashIndex + 1) + "-file.dot";

        //获取项目中每个文件所对应的节点编号
        Map<String, Integer> fileToNodeMap = callGraph.getNodeMapping("DeveloperContributionEvaluation/CallGraphs/" + callGraphName);
//        for (String key : fileToNodeMap.keySet()) {
//            System.out.println("Key: " + key + ", Value: " + fileToNodeMap.get(key)); //输出键值对验证是否正确
//        }


    }


    //生成两个commit之间有变化的.java文件之间的编辑脚本
    public static void getEditScriptsBetweenCommits(String gitDirectory, String newCommit, String oldCommit) throws IOException {
        List<String> changedJavaFiles = getChangedJavaFiles(gitDirectory, newCommit, oldCommit);

        System.out.println("这两个commit之间所有的编辑脚本如下：");
        String filePath = "DeveloperContributionEvaluation/tempFile/" + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/";
        // 确保文件夹存在，如果不存在则创建它
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        int cnt = 0;
        for(String fileNameLong:changedJavaFiles){
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
            System.out.println(++cnt);
//            System.out.println("成功获取两个commit中文件" + fileNameShort + "的内容");


            //将该文件内容保存到临时文件夹tempFile中
            String newFileName = filePath + "new_" + fileNameShort;
            String oldFileName = filePath + "old_" + fileNameShort;
            writeStringToFile(fileContentAtNewCommit, newFileName);
            writeStringToFile(fileContentAtOldCommit, oldFileName);

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
        String filesInCommit = executeGitCommand(gitDirectory, command);
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
        String fileContent = executeGitCommand(gitDirectory, command);
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
                System.err.println("命令执行失败，返回码：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return commitHashes;
    }

    //获取两个commit之间所有有更改的.java文件的名称
    public static List<String> getChangedJavaFiles(String gitDirectory, String commit1, String commit2) {
        List<String> changedFiles = new ArrayList<>();

        try {
            // 创建 ProcessBuilder 对象，执行 git diff --name-only 命令获取文件变更列表
            ProcessBuilder builder = new ProcessBuilder("git", "diff", "--name-only", commit1, commit2);
            builder.directory(new File(gitDirectory));//设置命令工作目录

            // 启动进程
            Process process = builder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("发生变更的.java文件如下：");
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(".java")) { // 只添加 .java 文件名
                    changedFiles.add(line);
                    System.out.println(line);
                }
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("命令执行失败，返回码：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return changedFiles;
    }

    //用于在特定目录中执行git命令，如executeGitCommand(gitDirectory, new String[]{"git", "log"});
    public static String executeGitCommand(String gitDirectory, String[] command) {

        StringBuilder output = new StringBuilder();

        try {
            // 创建 ProcessBuilder 对象，指定命令
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(gitDirectory)); //设置命令工作目录
            builder.redirectErrorStream(true); // 合并标准输出和错误输出流

            // 启动进程
            Process process = builder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
//                System.out.println(line); // 输出命令输出
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("命令执行失败，返回码：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output.toString();
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
    public static void readFile()throws IOException{
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

    //从源代码提取出抽象语法树
    public static void getASTFromFile(String filePath) throws Exception {
        Run.initGenerators(); // registers the available parsers
        TreeContext tc = TreeGenerators.getInstance().getTree(filePath); // retrieves and applies the default parser for the file
        Tree t = (Tree) tc.getRoot(); // retrieves the root of the tree
//        System.out.println(t.toTreeString()); // displays the tree in our ad-hoc format
//        System.out.println(TreeIoUtils.toLisp(tc).toString()); // displays the tree in LISP syntax

        // AST 的字符串表示
        String astString = t.toTreeString();

        // 获取 Java 文件的目录路径
        String javaFileDirectory = new File(filePath).getParent();
//        System.out.println(javaFileDirectory);
        // 构建 AST 文件的文件夹路径
//        String astFolder = javaFileDirectory + "/ASTfiles/";
        String astFolder = "DeveloperContributionEvaluation/ASTfiles/";

        // 确保文件夹存在，如果不存在则创建它
        File folder = new File(astFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 指定要写入的文件名和路径
        String srcFileName = new File(filePath).getName();
        String astFileName = astFolder + srcFileName.replace(".java", "") + "_AST.txt";

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
        for (Action action : actions) {
//            System.out.println(action);
        }

        String filePath = "DeveloperContributionEvaluation/editScripts/";
        if(!oldCommit.equals("")){
            filePath = filePath + oldCommit.substring(0,7) + "_to_" + newCommit.substring(0,7) + "/";
            // 确保文件夹存在，如果不存在则创建它
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        String editScriptFileName = filePath + "edit_script_between_" +
                new File(srcFile).getName().replace(".java", "") + "_and_" + new File(dstFile).getName().replace(".java", "") + ".txt";
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
