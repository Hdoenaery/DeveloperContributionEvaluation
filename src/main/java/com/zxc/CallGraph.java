package com.zxc;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CallGraph {

    //传入调用图以获得每个文件与它所对应的节点编号的映射Map
    public static Map<String, Integer> getNodeMapping(String callGraphPath) {
        Map<String, Integer> fileToNodeMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(callGraphPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//")) {
                    int firstColonIndex = line.indexOf(':'); // 找到第一个出现的冒号的索引
                    if (firstColonIndex != -1) { // 确保找到了冒号
                        int nodeNumber = Integer.parseInt(line.substring(3, firstColonIndex));
                        String fileNameLong = line.substring(firstColonIndex + 1).trim();
                        fileToNodeMap.put(fileNameLong, nodeNumber);
                    }
                } else break;
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return fileToNodeMap;
    }
    //传入调用图以获得各个节点的得分

    //传入项目源代码路径以获取该项目的调用图
    public static void getCallGraph(String src){

        String outputDirectory = "E:\\IDEA\\maven-project\\DeveloperContributionEvaluation\\CallGraphs";
        int lastSlashIndex = src.lastIndexOf("\\");
        String fileName = src.substring(lastSlashIndex + 1); //从文件路径中提取出文件名
//        System.out.println("filename = " + fileName);
        String command = "depends -f dot -d " + outputDirectory +
                " java " + src + " " + fileName;
//        System.out.println(command);
        executeCmdCommand("E:/Postgraduate_study/depends-0.9.7", command);

    }


    //用于在指定目录执行cmd命令
    public static void executeCmdCommand(String workPath, String command) {
        try {
            // 构建命令
            String fullCommand = "cmd.exe /c " + command;

            // 设置执行路径
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.directory(new File(workPath));

            // 启动进程
            Process process = builder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);//输出命令执行结果
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("命令执行失败，返回码：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
