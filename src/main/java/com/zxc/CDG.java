package com.zxc;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 定义邻接表类
class AdjacencyList {
    Map<String, List<String>> forwardAdjacencyList = new HashMap<>();//正向邻接表，记录当前节点的儿子列表
    Map<String, List<String>> backwardAdjacencyList = new HashMap<>();//反向邻接表，记录当前节点的父亲列表

    //添加正向边，从父亲到儿子
    public void addForwardEdge(String sourceID, String targetID) {
        // 向邻接表中插入节点
        forwardAdjacencyList.computeIfAbsent(sourceID, k -> new ArrayList<>()).add(targetID);
        //computeIfAbsent(source, k -> new ArrayList<>()) 是一个 Map 接口中的方法。
        //它的作用是根据指定的键 source（源节点）在 Map 中查找对应的值，如果找到了则返回该值；
        //如果未找到，则使用提供的 Lambda 表达式 k -> new ArrayList<>() 创建一个新的空列表，并将其放入 Map 中，然后返回这个新创建的列表。
    }
    //添加反向边，从儿子到父亲
    public void addBackwardEdge(String sourceID, String targetID) {
        backwardAdjacencyList.computeIfAbsent(targetID, k -> new ArrayList<>()).add(sourceID);
    }
}
public class CDG {
    public static void main(String[] args) throws FileNotFoundException {
        Set<Node> oldNodes = new HashSet<>();
        Set<Node> newNodes = new HashSet<>();
        Set<Edge> oldEdges = new HashSet<>();
        Set<Edge> newEdges = new HashSet<>();
        AdjacencyList newAdjacencyList = new AdjacencyList();
        AdjacencyList oldAdjacencyList = new AdjacencyList();

        // 解析两个版本的.dot文件
        parseDotFile("E:\\IDEA\\maven-project\\DeveloperContributionEvaluation\\PDGs\\5e5ba4b\\old_PipePool_cdg\\1-cdg.dot",
                oldNodes, oldEdges, oldAdjacencyList);
        parseDotFile("E:\\IDEA\\maven-project\\DeveloperContributionEvaluation\\PDGs\\5e5ba4b\\new_PipePool_cdg\\1-cdg.dot",
                newNodes, newEdges, newAdjacencyList);

        // 输出邻接表内容以检查
//        for (Map.Entry<String, List<String>> entry : newAdjacencyList.forwardAdjacencyList.entrySet()) {
//            String key = entry.getKey();
//            List<String> values = entry.getValue();
//            System.out.println("Key: " + key);
//            System.out.println("Values: " + values);
//        }

        // 查找发生更改的节点
        Set<Node> changedNodes = findChangedConditionalNodes(oldNodes, newNodes, newAdjacencyList);

        // 输出结果
        System.out.println("Changed Nodes:");
        for (Node node : changedNodes) {
            System.out.println(node.id + ": " + node.label + "   " + node.lineNumber);
        }

        // 计算正向跟踪所涉及的节点总数
        Set<Node> forwardTraversal = new HashSet<>();
        for(Node startNode:changedNodes) {
            traverseForward(startNode, forwardTraversal, newEdges);
        }
        System.out.println("Forward Traversal Node Count: " + forwardTraversal.size());

//        double CDG_impact = Math.max(changedNodes.size() * 1.0 / newNodes.size(), 0);
        double CDG_impact = Math.max(forwardTraversal.size() * 1.0 / newNodes.size(), 0);
        System.out.println(CDG_impact);
        System.out.println(changedNodes.size());
        System.out.println(newNodes.size());


    }
    //计算CDG_impact
    public static double getCDGimpact(String src, String className, String methodName) throws FileNotFoundException {
        String oldDirectoryName = "old_" + className + "_cdg";
        String newDirectoryName = "new_" + className + "_cdg";

        Set<Node> oldNodes = new HashSet<>();
        Set<Node> newNodes = new HashSet<>();
        Set<Edge> oldEdges = new HashSet<>();
        Set<Edge> newEdges = new HashSet<>();
        AdjacencyList newAdjacencyList = new AdjacencyList();
        AdjacencyList oldAdjacencyList = new AdjacencyList();

//        System.out.println("className:  " + className);
//        System.out.println("methodName:  " + methodName);
        String oldCDGfilePath = getCDGfilePath(src + "/" + oldDirectoryName, className, methodName);
        String newCDGfilePath = getCDGfilePath(src + "/" + newDirectoryName, className, methodName);
//        System.out.println("oldPath:  " + oldCDGfilePath);
//        System.out.println("newPath:  " + newCDGfilePath);

        // 解析两个版本的.dot文件
        if(oldCDGfilePath != "")
            parseDotFile(oldCDGfilePath, oldNodes, oldEdges, oldAdjacencyList);
        if(newCDGfilePath != "")
            parseDotFile(newCDGfilePath, newNodes, newEdges, newAdjacencyList);


        // 查找发生更改的条件语句节点
        Set<Node> changedNodes = findChangedConditionalNodes(oldNodes, newNodes, newAdjacencyList);

//        // 输出结果
//        System.out.println("Changed Nodes:");
//        for (Node node : changedNodes) {
//            System.out.println(node.id + ": " + node.label);
//        }

        // 计算正向跟踪所涉及的节点总数
        Set<Node> forwardTraversal = new HashSet<>();
        for(Node startNode:changedNodes) {
            traverseForward(startNode, forwardTraversal, newEdges);
        }
//        System.out.println("Forward Traversal Node Count: " + forwardTraversal.size());


        double CDG_impact = 0;
        if(newNodes.size() > 0) {
            CDG_impact = forwardTraversal.size() * 1.0 / newNodes.size();
//            CDG_impact = changedNodes.size() * 1.0 / newNodes.size();
        }

        return CDG_impact;
    }

    //获取className类的methodName方法所对应的CDG文件路径
    public static String getCDGfilePath(String directoryPath, String className, String methodName) {
        // 构建目标文件夹路径
        File directory = new File(directoryPath);

        // 检查目标文件夹是否存在
        if (!directory.exists() || !directory.isDirectory()) {
//            System.out.println("getCDGfilePath,目录不存在或不是一个有效的目录：" + directoryPath);
            return "";
        }

        // 遍历目标文件夹下的所有文件
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                // 判断是否为文件
                if (file.isFile()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String firstLine = reader.readLine();
                        if (className.equals(methodName)) {
//                            System.out.println("class = " + className);
//                            System.out.println("method = " + methodName);
                            if (firstLine != null && firstLine.contains("\"&lt;init&gt;\"")) {
//                                System.out.println(file.getPath());
                                return file.getPath();
                            }
                        } else if (firstLine != null && firstLine.contains("\"" + methodName + "\"")) {
//                            System.out.println(methodName);
//                            System.out.println(file.getPath());
                            return file.getPath();
//                            String line;
//                            // 逐行读取文件内容并输出
//                            while ((line = reader.readLine()) != null) {
//                                System.out.println(line);
//                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }

    // 查找发生更改的条件语句节点(有多个后继节点的节点即认为是条件语句节点)
    public static Set<Node> findChangedConditionalNodes(Set<Node> oldNodes, Set<Node> newNodes, AdjacencyList adjacencyList) {
        Set<Node> changedNodes = new HashSet<>();
        for (Node newNode : newNodes) {
//            if ((newNode.label.contains("lessThan") || newNode.label.contains("greaterThan") || newNode.label.contains("equals")) && !oldNodes.contains(newNode)) {
            if (adjacencyList.forwardAdjacencyList.containsKey(newNode.id) &&
                    adjacencyList.forwardAdjacencyList.get(newNode.id).size() > 1) {
                boolean flag = true;
                for(Node oldNode : oldNodes) {
                    if(oldNode.label.equals(newNode.label)) {
                        flag = false;
                        break;
                    }
                }
                if(flag)
                    changedNodes.add(newNode);
            }
        }
        return changedNodes;
    }

    // 解析.dot文件
    public static void parseDotFile(String fileName, Set<Node> nodes, Set<Edge> edges, AdjacencyList adjacencyList) {
        File file = new File(fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // 有些节点跨了多行，需要特殊处理
            String previousLine = "";
            while ((line = reader.readLine()) != null) {
//                System.out.println("line = " + line);
                if(line.trim().endsWith("{") || line.trim().endsWith("}"))
                    continue;

                if(!line.trim().endsWith("]") && !line.contains("->")) {
                    previousLine += line;
                    continue;
                }
                if(!previousLine.equals("")) {
                    line = previousLine + line;
                    previousLine = "";
                }

//                System.out.println(line);
                if (line.startsWith("\"")) {
                    //                System.out.println(line);
                    // 解析节点
                    String[] parts = line.split("\\[label = ");

                    String id = parts[0].trim().replace("\"", "").trim();
                    String label = parts[1].trim().replace("]", "").trim();

                    Pattern pattern = Pattern.compile("<SUB>(\\d+)</SUB>"); //提取行号的正则表达式
                    Matcher matcher = pattern.matcher(label);
                    String lineNumber = "";

                    if (matcher.find()) {
                        lineNumber = matcher.group(1);
                    }
                    label = label.replace("<SUB>", "").replace("</SUB>", "");
                    label = label.substring(1, label.lastIndexOf(")") + 1);

//                    System.out.println("id = " + id + "   label = " + label + "   lineNumber = " + lineNumber);


                    Node node = new Node(id, label, lineNumber);
                    nodes.add(node);
                } else if (line.contains("->")) {
//                    System.out.println(line);
                    // 解析边
                    String[] parts = line.split("->");
                    String sourceId = parts[0].trim().replace("\"", "").trim();
                    String targetId = parts[1].trim().split("\\[")[0].replace("\"", "").trim();
//                    System.out.println("s = " + sourceId + "   t = " + targetId);
                    adjacencyList.addForwardEdge(sourceId, targetId);
                    adjacencyList.addBackwardEdge(sourceId, targetId);
                    Node source = getNodeById(nodes, sourceId);
                    Node target = getNodeById(nodes, targetId);
//                    System.out.println("source = " + source);
//                    System.out.println("target = " + target);
                    String label = "";
                    String [] temp = line.split("\\[");
                    if(temp.length > 1) {
                        label = temp[1].trim().split("=")[1].trim();
                    }
                    Edge edge = new Edge(source, target, label);
                    edges.add(edge);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 正向遍历
    public static void traverseForward(Node node, Set<Node> visited, Set<Edge> edges) {
        if (!visited.contains(node)) {
            visited.add(node);
            for (Edge edge : edges) {
                if (edge.source.equals(node)) {
                    traverseForward(edge.target, visited, edges);
                }
            }
        }
    }

    // 根据节点id查找节点
    public static Node getNodeById(Set<Node> nodes, String id) {
        for (Node node : nodes) {
            if (node.id.equals(id)) {
                return node;
            }
        }
        return null;
    }

    //生成对应文件的CDG
    public static void getCDG(String src, String newCommit) {
        String basePath = "E:/IDEA/maven-project/DeveloperContributionEvaluation/PDGs";
        String folderName = newCommit.substring(0,7);
        String folderPath = basePath + "/" + folderName;

        // 获取目录下的所有文件和子目录
        File directory = new File(src);
        File[] files = directory.listFiles();

        // 如果目录为空，则直接返回
        if (files == null) {
            return;
        }
        for (File file : files) {
            // 构建命令
            List<String> command = new ArrayList<>();
            command.add("cmd");
            command.add("/c");
            command.add("E:/Postgraduate_study/joern-cli/joern-export");
            command.add(file.getName().replace(".java", "") + ".bin");
            command.add("--repr");
            command.add("cdg");
            command.add("--out");
            command.add(file.getName().replace(".java", "") + "_cdg");

            // 创建 ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(folderPath));

            try {
                // 启动进程
                Process process = pb.start();

                // 等待进程结束
                int exitCode = process.waitFor();

                // 打印输出
                if (exitCode == 0) {
                    System.out.println("joern-export cdg执行成功  " + file.getName());
                } else {
                    System.out.println("joern-export cdg执行失败，exit code：" + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
