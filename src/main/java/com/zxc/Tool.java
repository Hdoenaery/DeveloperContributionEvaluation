package com.zxc;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tool {

    //获取两次commit之间发生变化的方法
    public static List<String> getChangedMethods(String gitDirectory, String oldCommit, String newCommit) {
//        String diffContent = executeGitCommand(gitDirectory, new String[]{"git", "diff", "-U50", oldCommit, newCommit});
        List<String> changedMethods = new ArrayList<>();
        Map<String, Integer> LOC = new HashMap<>();
        Map<String, Integer> CC = new HashMap<>();
        try {
            // 创建 ProcessBuilder 对象来启动 Python 进程
//            ProcessBuilder processBuilder = new ProcessBuilder("python", "-c",
//                    "from D:/Python_work/getChangedMethods.py import getChangedMethods; "
//                            + "print(getChangedMethods('" + gitDirectory + "', '" + oldCommit + "', '" + newCommit + "'))");

            ProcessBuilder processBuilder = new ProcessBuilder("python", "DeveloperContributionEvaluation/pythonScripts/getChangedMethods.py",
                    gitDirectory, oldCommit, newCommit);

            // 重定向错误流到标准输出流
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取 Python 进程的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int cnt = 0;
            while ((line = reader.readLine()) != null) {
                cnt++;
                if(cnt % 5 == 1) {
                    changedMethods.add(line);
                }
            }

            // 等待 Python 进程退出，并获取退出代码
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Python process exited with error code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return changedMethods;
    }


    //输出字符串中所有字符的 Unicode 编码（包含不可见字符）,用于查看从文件中读取的字符串中是否包含不可见字符
    public static void printInvisibleCharacters(String str) {
        // 将字符串转换为字符数组
        char[] charArray = str.toCharArray();
        // 遍历字符数组
        for (char c : charArray) {
            // 检查字符是否为不可见字符
            if (Character.isISOControl(c)) {
                // 输出字符及其 Unicode 编码
                System.out.println("Invisible Character: " + ", Unicode: \\u" + Integer.toHexString(c | 0x10000).substring(1));
            }else{
                System.out.println("Character: " + c + ", Unicode: \\u" + Integer.toHexString(c | 0x10000).substring(1));
            }
        }
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
}
