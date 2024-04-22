package com.zxc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 定义节点类
class Node {
    String id;
    String label;
    String lineNumber;

    public Node(String id, String label, String lineNumber) {
        this.id = id;
        this.label = label;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        return id.equals(other.id) && label.equals(other.label);
//        return label.equals(other.label);  //Node的label相同即认为是同一节点
    }
    @Override
    public int hashCode() {
//        return Objects.hash(id);
        return Objects.hash(label);
    }
}

// 定义边类
class Edge {
    Node source;
    Node target;
    String label;

    public Edge(Node source, Node target, String label) {
        this.source = source;
        this.target = target;
        this.label = label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Edge other = (Edge) obj;
        return source.equals(other.source) && target.equals(other.target) && label.equals(other.label);
    }
}
public class DDG {
    public static void main(String[] args) throws IOException {
        Set<Node> oldNodes = new HashSet<>();
        Set<Node> newNodes = new HashSet<>();
        Set<Edge> oldEdges = new HashSet<>();
        Set<Edge> newEdges = new HashSet<>();

        // 解析两个版本的.dot文件
        parseDotFile("E:\\IDEA\\maven-project\\DeveloperContributionEvaluation\\PDGs\\5e5ba4b\\old_Bird_ddg\\0-ddg.dot", oldNodes, oldEdges);
        parseDotFile("E:\\IDEA\\maven-project\\DeveloperContributionEvaluation\\PDGs\\5e5ba4b\\new_Bird_ddg\\0-ddg.dot", newNodes, newEdges);


        // 查找发生更改的节点
        Set<Node> changedNodes = findChangedAssignmentNodes(oldNodes, newNodes);

        // 输出结果
        System.out.println("Changed Nodes:");
        for (Node node : changedNodes) {
            System.out.println(node.id + ": " + node.label);
        }

        // 计算正向跟踪所涉及的节点总数
        Set<Node> forwardTraversal = new HashSet<>();
        for(Node startNode:changedNodes) {
            traverseForward(startNode, forwardTraversal, newEdges);
        }
        System.out.println("Forward Traversal Node Count: " + forwardTraversal.size());

        // 计算反向跟踪所涉及的节点总数
        Set<Node> backwardTraversal = new HashSet<>();
        for(Node startNode:changedNodes) {
            traverseBackward(startNode, backwardTraversal, newEdges);
        }
        System.out.println("Backward Traversal Node Count: " + backwardTraversal.size());

        // 将 forwardTraversal 中的元素添加到 backwardTraversal 中，自动去重
        backwardTraversal.addAll(forwardTraversal);
        System.out.println("All Traversal Node Count: " + backwardTraversal.size());

        double DDG_impact = Math.max(backwardTraversal.size() * 1.0 / newNodes.size(), 0);
        System.out.println(DDG_impact);
        System.out.println(oldNodes.size());
        System.out.println(newNodes.size());


    }

    //计算DDG_impact
    public static double getDDGimpact(String src, String className, String methodName) {
        String oldDirectoryName = "old_" + className + "_ddg";
        String newDirectoryName = "new_" + className + "_ddg";

        Set<Node> oldNodes = new HashSet<>();
        Set<Node> newNodes = new HashSet<>();
        Set<Edge> oldEdges = new HashSet<>();
        Set<Edge> newEdges = new HashSet<>();

//        System.out.println("className:  " + className);
//        System.out.println("methodName:  " + methodName);
        String oldDDGfilePath = getDDGfilePath(src + "/" + oldDirectoryName, className, methodName);
        String newDDGfilePath = getDDGfilePath(src + "/" + newDirectoryName, className, methodName);
//        System.out.println("oldPath:  " + oldDDGfilePath);
//        System.out.println("newPath:  " + newDDGfilePath);

        // 解析两个版本的.dot文件
        if(oldDDGfilePath != "")
            parseDotFile(oldDDGfilePath, oldNodes, oldEdges);
        if(newDDGfilePath != "")
            parseDotFile(newDDGfilePath, newNodes, newEdges);


        // 查找发生更改的节点
        Set<Node> changedNodes = findChangedAssignmentNodes(oldNodes, newNodes);

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

        // 计算反向跟踪所涉及的节点总数
        Set<Node> backwardTraversal = new HashSet<>();
        for(Node startNode:changedNodes) {
            traverseBackward(startNode, backwardTraversal, newEdges);
        }
//        System.out.println("Backward Traversal Node Count: " + backwardTraversal.size());

        // 将 forwardTraversal 中的元素添加到 backwardTraversal 中，自动去重
        backwardTraversal.addAll(forwardTraversal);
//        System.out.println("All Traversal Node Count: " + backwardTraversal.size());

        double DDG_impact = Math.max(backwardTraversal.size() * 1.0 / newNodes.size(), 0);
//        System.out.println(DDG_impact);
//        System.out.println(newNodes.size());
        return DDG_impact;
    }

    //获取className类的methodName方法所对应的DDG文件路径
    public static String getDDGfilePath(String directoryPath, String className, String methodName) {
        // 构建目标文件夹路径
        File directory = new File(directoryPath);

        // 检查目标文件夹是否存在
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("getDDGfilePath,目录不存在或不是一个有效的目录：" + directoryPath);
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

    // 查找发生更改的assignment节点
    public static Set<Node> findChangedAssignmentNodes(Set<Node> oldNodes, Set<Node> newNodes) {
        Set<Node> changedNodes = new HashSet<>();
        for (Node newNode : newNodes) {
            if (newNode.label.contains("assignment") && !oldNodes.contains(newNode)) {
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
    public static void parseDotFile(String fileName, Set<Node> nodes, Set<Edge> edges) {
        File file = new File(fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // 有些节点跨了多行，需要特殊处理
            String previousLine = "";
            while ((line = reader.readLine()) != null) {
//                System.out.println("line = " + line);
                if(line.trim().startsWith("digraph") || line.trim().equals("}"))
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

    //                System.out.println("id = " + id + "   label = " + label + "   lineNumber = " + lineNumber);


                    Node node = new Node(id, label, lineNumber);
                    nodes.add(node);
                } else if (line.contains("->")) {
//                    System.out.println(line);
                    // 解析边
                    String[] parts = line.split("->");
                    String sourceId = parts[0].trim().replace("\"", "").trim();
                    String targetId = parts[1].trim().split("\\[")[0].replace("\"", "").trim();
//                    System.out.println("s = " + sourceId + "   t = " + targetId);
                    Node source = getNodeById(nodes, sourceId);
                    Node target = getNodeById(nodes, targetId);
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

    // 根据节点id查找节点
    public static Node getNodeById(Set<Node> nodes, String id) {
        for (Node node : nodes) {
            if (node.id.equals(id)) {
                return node;
            }
        }
        return null;
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

    // 反向遍历
    public static void traverseBackward(Node node, Set<Node> visited, Set<Edge> edges) {
        if (!visited.contains(node)) {
            visited.add(node);
            for (Edge edge : edges) {
                if (edge.target.equals(node)) {
                    traverseBackward(edge.source, visited, edges);
                }
            }
        }
    }

    //生成对应文件的DDG
    public static void getDDG(String src, String newCommit) {
        String basePath = "E:/IDEA/maven-project/DeveloperContributionEvaluation/PDGs";
        String folderName = newCommit.substring(0,7);
        String folderPath = basePath + "/" + folderName;
        // 创建File对象，表示文件夹路径
        File folder = new File(folderPath);

        // 检查文件夹是否已存在
        if (!folder.exists()) {
            // 如果不存在，则创建文件夹
            boolean created = folder.mkdir();
            if (!created) {
                System.out.println("无法创建文件夹：" + folderPath);
            }
        }
        // 获取目录下的所有文件和子目录
        File directory = new File(src);
        File[] files = directory.listFiles();

        // 如果目录为空，则直接返回
        if (files == null) {
            return;
        }

        for (File file : files) {
//            System.out.println(file.getName());
            // 构建第一个命令
            List<String> command1 = new ArrayList<>();
            command1.add("cmd");
            command1.add("/c");
            command1.add("E:/Postgraduate_study/joern-cli/joern-parse");
            command1.add(src + file.getName());
            command1.add("-o");
            command1.add(file.getName().replace(".java", "") + ".bin");

            // 创建第一个 ProcessBuilder
            ProcessBuilder pb1 = new ProcessBuilder(command1);
            pb1.directory(new File(folderPath));

            try {
                // 启动第一个进程
                Process process1 = pb1.start();

                // 等待第一个进程结束
                int exitCode1 = process1.waitFor();

                // 打印第一个命令的输出
                if (exitCode1 == 0) {
                    System.out.println("joern-parse ddg执行成功  " + command1.get(5));

                    // 构建第二个命令
                    List<String> command2 = new ArrayList<>();
                    command2.add("cmd");
                    command2.add("/c");
                    command2.add("E:/Postgraduate_study/joern-cli/joern-export");
                    command2.add(file.getName().replace(".java", "") + ".bin");
                    command2.add("--repr");
                    command2.add("ddg");
                    command2.add("--out");
                    command2.add(file.getName().replace(".java", "") + "_ddg");

                    // 创建第二个 ProcessBuilder
                    ProcessBuilder pb2 = new ProcessBuilder(command2);
                    pb2.directory(new File(folderPath));

                    // 启动第二个进程
                    Process process2 = pb2.start();

                    // 等待第二个进程结束
                    int exitCode2 = process2.waitFor();

                    // 打印第二个命令的输出
                    if (exitCode2 == 0) {
                        System.out.println("joern-export ddg执行成功" + command1.get(5));
                    } else {
                        System.out.println("joern-export ddg执行失败，exit code：" + exitCode2);
                    }
                } else {
                    System.out.println("joern-parse ddg执行失败，exit code：" + exitCode1);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
