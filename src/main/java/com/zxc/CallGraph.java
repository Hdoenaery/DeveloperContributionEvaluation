package com.zxc;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallGraph {
    Map<Integer, List<Integer>> forwardAdjacencyList = new HashMap<>();
    Map<Integer, List<Integer>> backwardAdjacencyList = new HashMap<>();

    //解析调用图.dot文件，构建邻接表存图
    public Map<Integer, List<Integer>> buildGraph(String callGraphPath) {

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
                    this.forwardAdjacencyList.computeIfAbsent(father, k -> new ArrayList<>()).add(son);
                    this.backwardAdjacencyList.computeIfAbsent(son, k -> new ArrayList<>()).add(father);
                    //computeIfAbsent(source, k -> new ArrayList<>()) 是一个 Map 接口中的方法。
                    //它的作用是根据指定的键 source（源节点）在 Map 中查找对应的值，如果找到了则返回该值；
                    //如果未找到，则使用提供的 Lambda 表达式 k -> new ArrayList<>() 创建一个新的空列表，并将其放入 Map 中，然后返回这个新创建的列表。
                    if(!this.forwardAdjacencyList.containsKey(son)) {
                        this.forwardAdjacencyList.put(son, new ArrayList<>());
                    }
                    if(!this.backwardAdjacencyList.containsKey(father)) {
                        this.backwardAdjacencyList.put(father, new ArrayList<>());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.forwardAdjacencyList;
    }
    public Map<Integer, List<Integer>> buildBackwardGraph(){
        return backwardAdjacencyList;
    }
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
    public static Map<Integer, Double> getPageRank(Map<Integer, List<Integer>> graphC, Map<Integer, List<Integer>> backwardGraphC) {
        // 初始化每个节点的PageRank值为1.0
//        System.out.println(graphC.size());
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
//            System.out.println("PageRank迭代次数 = " + i);
            Map<Integer, Double> newPageRank = new HashMap<>();
            sum = 0;
            // 遍历每个节点
            for (Integer node : graphC.keySet()) {
                double rank = (1 - dampingFactor); // 初始化PageRank值

//                // 遍历所有指向node的节点
//                for (Integer incomingNode : graphC.keySet()) {
//                    if (graphC.get(incomingNode).contains(node)) {
//                        int outgoingLinks = graphC.get(incomingNode).size();//求指向node
//                        rank += dampingFactor * (pageRank.get(incomingNode) / outgoingLinks);
//                    }
//                }
                for (Integer incomingNode : backwardGraphC.get(node)) {
                    int outgoingLinks = graphC.get(incomingNode).size();//求入链结点的出边数
                    rank += dampingFactor * (pageRank.get(incomingNode) / outgoingLinks);
                }
                newPageRank.put(node, rank);
                sum += rank;
            }

            // 更新PageRank值
            pageRank = newPageRank;
        }

//        System.out.println(sum);
        for(Integer node :
                pageRank.keySet()){
//            System.out.println("node = " + node + "   pagerank = " + pageRank.get(node));
            pageRank.replace(node, pageRank.get(node) / sum);
        }
        return pageRank;
    }

    //传入调用图以获得每个函数与它所对应的节点编号的映射Map
    public static Map<String, Integer> getNodeMapping(String callGraphPath) {
        Map<String, Integer> methodToNodeMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(callGraphPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//")) {
                    String className = null;
                    String methodName = null;
                    int nodeNumber = 0;
                    // 定义正则表达式
                    String regex = "// (\\d+).*\\\\([^.]+).*\\.([^)]+)\\)";

                    // 编译正则表达式
                    Pattern pattern = Pattern.compile(regex);

                    // 创建 Matcher 对象
                    Matcher matcher = pattern.matcher(line);

                    // 查找匹配
                    if (matcher.find()) {
                        // 提取类名和方法名
//                        System.out.println(line);
//                        System.out.println(matcher.group(1));
//                        System.out.println(matcher.group(2));
//                        System.out.println(matcher.group(3));
                        nodeNumber = Integer.parseInt(matcher.group(1));
                        className = matcher.group(2);
                        methodName = matcher.group(3);
                    }


                    methodToNodeMap.put(className + "::" + methodName, nodeNumber);
//                    int firstColonIndex = line.indexOf(':'); // 找到第一个出现的冒号的索引
//                    if (firstColonIndex != -1) { // 确保找到了冒号
//                        int nodeNumber = Integer.parseInt(line.substring(3, firstColonIndex));
//                        String fileNameLong = line.substring(firstColonIndex + 1).trim();
//                        System.out.println(fileNameLong);
//
//                    }
                } else break;
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return methodToNodeMap;
    }

    //传入项目源代码路径以获取该项目的调用图
    public static void getCallGraph(String src, String outputFormat, String granularity, String projectName, String newCommit){
        //记录获取调用图的开始时间
        long startTime = System.currentTimeMillis();
        String outputDirectory = "E:/IDEA/maven-project/DeveloperContributionEvaluation/CallGraphs";

        // 将路径字符串转换为File对象
        File directory = new File(outputDirectory);
        // 检查目录是否存在
        if (!directory.exists()) {
            // 如果目录不存在，则创建它
            if (directory.mkdirs()) {
//                System.out.println("目录已创建: " + outputDirectory);
            } else {
                System.out.println("目录创建失败: " + outputDirectory);
            }
        }

        String command = "depends -f " + outputFormat + " -g " + granularity + " -d " + outputDirectory +
                " java " + src + " " + projectName + "_" + newCommit.substring(0, 7);
        System.out.println(command);
        executeCmdCommand("E:/Postgraduate_study/depends-0.9.7", command);
        // 记录获取调用图结束时间
        long nowTime = System.currentTimeMillis();
        // 计算总运行时间（毫秒）并将毫秒转换为秒
        double totalTimeInSeconds = (nowTime - startTime) / 1000.0;
        System.out.println("本次getCallGraph计算的运行时间（秒）：" + totalTimeInSeconds);
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
