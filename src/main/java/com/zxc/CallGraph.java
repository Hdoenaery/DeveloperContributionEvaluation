package com.zxc;

import java.io.*;
import java.util.*;

public class CallGraph {


    //衡量函数间影响
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
//        System.out.println(node);
//        System.out.println(graphC.get(node));
        if (graphC.get(node).isEmpty()) {
//            System.out.println("mapPr.get(node) = " + mapPr.get(node));
            mapTmp.put(node, mapPr.get(node));//新值覆盖旧值
            return;
        }

        for (Integer child : graphC.get(node)) {
            if (!mapTmp.containsKey(child)) {
                process(child, graphC, mapPr, mapTmp, decay);
            }
            mapTmp.put(node, mapTmp.get(node) + mapTmp.get(child) * decay);
        }
    }


    // 传入有向图，获取每个节点的PageRank值
    public static Map<Integer, Double> getPageRank(Map<Integer, List<Integer>> graphC) {
        // 初始化每个节点的PageRank值为1.0
        Map<Integer, Double> pageRank = new HashMap<>();
        for (Integer node : graphC.keySet()) {
            pageRank.put(node, 1.0);
        }

        // 设置迭代次数和阻尼因子
        int iterations = 100;// 迭代次数
        double dampingFactor = 0.85;// 阻尼因子，一般取0.85

        double sum = 0;

        // 开始迭代计算PageRank值
        for (int i = 0; i < iterations; i++) {
            Map<Integer, Double> newPageRank = new HashMap<>();
            sum = 0;
            // 遍历每个节点
            for (Integer node : graphC.keySet()) {
                double rank = (1 - dampingFactor); // 初始化PageRank值

                // 遍历所有指向node的节点
                for (Integer incomingNode : graphC.keySet()) {
                    if (graphC.get(incomingNode).contains(node)) {
                        int outgoingLinks = graphC.get(incomingNode).size();//求指向node
                        rank += dampingFactor * (pageRank.get(incomingNode) / outgoingLinks);
                    }
                }
                newPageRank.put(node, rank);
                sum += rank;
            }

            // 更新PageRank值
            pageRank = newPageRank;
        }

//        System.out.println(sum);
        for(Integer node : pageRank.keySet()){
//            System.out.println(pageRank.get(node) / sum);
            pageRank.replace(node, pageRank.get(node) / sum);
        }
        return pageRank;
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

    //传入调用图以获得每个函数与它所对应的节点编号的映射Map
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

    //传入项目源代码路径以获取该项目的调用图
    public static void getCallGraph(String src, String outputFormat, String granularity){

        String outputDirectory = "E:\\IDEA\\maven-project\\DeveloperContributionEvaluation\\CallGraphs";
        int lastSlashIndex = src.lastIndexOf("\\");
        String fileName = src.substring(lastSlashIndex + 1); //从文件路径中提取出文件名
//        System.out.println("filename = " + fileName);
        String command = "depends -f " + outputFormat + " -g " + granularity + " -d " + outputDirectory +
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
//                System.out.println(line);//输出命令执行结果
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
