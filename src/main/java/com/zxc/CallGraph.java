package com.zxc;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallGraph {


    public Map<Integer, Double> measureInterFunctionInteraction (Map<Integer, List<Integer>> graphC, Map<Integer, Double> mapPr, double decay) {
        Map<Integer, Double> mapOut = new HashMap<>();
        Map<Integer, Double> mapTmp = new HashMap<>();

        for(Integer node:graphC.keySet()) {
            if(!mapTmp.containsKey(node)) {
                process(node, graphC, mapPr, mapTmp, decay);
            }
        }

        for(Integer node:mapPr.keySet()) {
            mapOut.put(node, mapPr.get(node) + (mapTmp.containsKey(node) ? mapTmp.get(node) : 0));
        }

        return mapOut;
    }

    private void process(Integer node, Map<Integer, List<Integer>> graphC, Map<Integer, Double> mapPr, Map<Integer, Double> mapTmp, double decay) {
        mapTmp.put(node, 0.0);
        System.out.println(node);
        System.out.println(graphC.get(node));
        if (graphC.get(node).isEmpty()) {
            System.out.println("mapPr.get(node) = " + mapPr.get(node));
            mapTmp.put(node, mapPr.get(node));//新值覆盖旧值
            return;
        }

        for (Integer child : graphC.get(node)) {
            if (!mapTmp.containsKey(child)) {
                process(child, graphC, mapPr, mapTmp, decay);
            }
            System.out.println("node = " + node);
            mapTmp.put(node, mapTmp.get(node) + mapTmp.get(child) * decay);
        }
    }
    //解析调用图.dot文件，构建邻接表存图
    public static Map<Integer, List<Integer>> buildGraph(String callGraphPath) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(callGraphPath))) {
            String line;
            boolean isInsideGraph = false;

            int cnt = 0;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("//") || line.startsWith("digraph") || line.startsWith("{") || line.startsWith("}")) {
                    continue;
                }


                line = line.replace(";", "");//去除行尾分号
//                System.out.println(line);
                //\\s* 表示零个或多个空白字符，包括空格、制表符等。split("\\s*->\\s*") 将会以箭头符号 -> 作为分隔符来拆分字符串，且忽略箭头两边的任何空白字符。
                String[] parts = line.trim().split("\\s*->\\s*");
                if (parts.length == 2) {
                    int father = Integer.parseInt(parts[0].trim());
                    int son = Integer.parseInt(parts[1].trim());
                    // 向邻接表中插入节点
                    adjacencyList.computeIfAbsent(father, k -> new ArrayList<>()).add(son);
                    //computeIfAbsent(source, k -> new ArrayList<>()) 是一个 Map 接口中的方法。
                    //它的作用是根据指定的键 source（源节点）在 Map 中查找对应的值，如果找到了则返回该值；
                    //如果未找到，则使用提供的 Lambda 表达式 k -> new ArrayList<>() 创建一个新的空列表，并将其放入 Map 中，然后返回这个新创建的列表。
                    if(!adjacencyList.containsKey(son)) {
                        adjacencyList.put(son, new ArrayList<>());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return adjacencyList;
    }

    //传入调用图以获得每个文件与它所对应的节点编号的映射Map
    public static Map<String, Integer> getNodeFileMapping(String callGraphPath) {
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
