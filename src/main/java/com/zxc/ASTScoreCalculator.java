package com.zxc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Interval implements Comparable<Interval>{
    private int startPos;
    private int endPos;

    // 构造函数
    public Interval(int startPos, int endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    // 获取起始位置
    public int getStartPos() {
        return startPos;
    }

    // 设置起始位置
    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    // 获取结束位置
    public int getEndPos() {
        return endPos;
    }

    // 设置结束位置
    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    // 获取区间长度
    public int getLength() {
        return endPos - startPos + 1; // 区间长度包括起始位置和结束位置
    }

    // 判断是否重叠
    public boolean overlaps(Interval other) {
        return this.startPos <= other.endPos && other.startPos <= this.endPos;
    }

    // 判断是否包含某个位置
    public boolean contains(int pos) {
        return pos >= startPos && pos <= endPos;
    }

    // 重写toString方法
    @Override
    public String toString() {
        return "[" + startPos + ", " + endPos + "]";
    }

    // 实现 compareTo 方法
    // 实现 compareTo 方法
    @Override
    public int compareTo(Interval other) {
        // 首先比较起始位置
        int startComparison = Integer.compare(this.startPos, other.startPos);
        if (startComparison != 0) {
            return startComparison;
        }

        // 如果起始位置相同，再比较结束位置
        return Integer.compare(this.endPos, other.endPos);
    }
}
class IntervalComparator implements Comparator<Interval> {
    @Override
    public int compare(Interval interval1, Interval interval2) {
        // 比较区间的起始位置
        if (interval1.getStartPos() < interval2.getStartPos()) {
            return -1;
        } else if (interval1.getStartPos() > interval2.getStartPos()) {
            return 1;
        } else {
            // 如果起始位置相同，则比较结束位置
            if (interval1.getEndPos() < interval2.getEndPos()) {
                return -1;
            } else if (interval1.getEndPos() > interval2.getEndPos()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
class ClassNameAndInterval {
    private String className;
    private int startPos;
    private int endPos;

    // Constructor
    public ClassNameAndInterval(String className, int startPos, int endPos) {
        this.className = className;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    // Getters and Setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    @Override
    public String toString() {
        return "ClassNameAndInterval{" +
                "className='" + className + '\'' +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                '}';
    }
}

public class ASTScoreCalculator {

    enum EditType {
        //定义各个操作的得分
        INSERT_NODE(1.0), UPDATE_NODE(1.0), INSERT_TREE(1.0), MOVE_TREE(0.1),  DELETE_NODE(0.01), DELETE_TREE(0.01);

        private final double weight;

        EditType(double weight) {
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }
    }

//    public static void main(String[] args) {
////        String filePath = "DeveloperContributionEvaluation/editScripts/edit_script_between_old_Bug_for_Exception_and_new_Bug_for_Exception.txt";
//        String filePath = "DeveloperContributionEvaluation/editScripts/8fa7aea_to_e161fcd";
//        double score = calculateTotalASTScore(filePath);
//        System.out.println("Total AST Score: " + String.format("%.2f", score));
//
//    }

    // 解析文件夹editscriptsPath下所有编辑脚本，求出其中每一次操作是在哪个方法内部，并将该操作的得分加到该方法变更的总分上去
    public static Map<String, Double> calculateTotalASTScore(String editscriptsPath, String newCommit, String oldCommit, Map<String, String> fileChangedType) {
        Map<String, Double> astScore = new HashMap<>();  //key为方法名，value为该方法的ast得分

        File folder = new File(editscriptsPath);
//        if(!folder.exists()) {
//            //新增文件或删除文件的得分有待补充
//            return 0;
//        }

        File[] files = folder.listFiles(); // 获取文件夹下的所有文件

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) { // 确保是文件而不是文件夹
                    String publicClassName = file.getName().replace("editscript_", "").replace(".txt", "");
//                    System.out.println("\n@@@publicClassName = " + publicClassName);

                    String astFolder = "DeveloperContributionEvaluation/ASTfiles/";

                    if(fileChangedType.get(publicClassName).equals("D"))
                        astFolder += oldCommit.substring(0,7) + "/";
                    else
                        astFolder += newCommit.substring(0,7) + "/";

                    String astFilePath = astFolder + publicClassName + "_AST.txt";
                    TreeMap<Interval, String> intervalToMethodName = new TreeMap<>();//以method的区间来映射方法名

                    // 读取该编辑脚本文件对应的ast文件，提取出每个方法对应的位置
                    try (BufferedReader br = new BufferedReader(new FileReader(astFilePath))) {
                        String line;
                        // 匹配以ClassOrInterfaceDeclaration开头的行
                        Pattern classPattern = Pattern.compile("(ClassOrInterfaceDeclaration) \\[(\\d+),(\\d+)\\]");
                        // 匹配以MethodDeclaration或ConstructorDeclaration开头的行
                        Pattern declarationPattern = Pattern.compile("(MethodDeclaration|ConstructorDeclaration) \\[(\\d+),(\\d+)\\]");
                        // 匹配下下行的SimpleName
                        Pattern namePattern = Pattern.compile("SimpleName: (\\w+)");

                        List<ClassNameAndInterval> className = new ArrayList<>(); //用于匹配子类，这样子类中的方法名才会完整表示为“主类名::子类名::方法名”
                        String methodName = null;
                        while ((line = br.readLine()) != null) {
                            Matcher classMatcher = classPattern.matcher(line);//先尝试匹配类名
                            if (classMatcher.find()) {
                                // 如果匹配到了声明行，提取[startPos, endPos]
                                int classStartPos = Integer.parseInt(classMatcher.group(2));
                                int classEndPos = Integer.parseInt(classMatcher.group(3));
//                                System.out.println("Start Pos: " + classStartPos + ", End Pos: " + classEndPos);

                                // 一直往下读直到找到类名
                                while (true) {
                                    line = br.readLine();
                                    if (line == null) {
                                        break; // 文件已经结束
                                    }
                                    Matcher nameMatcher = namePattern.matcher(line);
                                    if (nameMatcher.find()) {
                                        // 如果匹配到了SimpleName，提取methodName
                                        className.add(new ClassNameAndInterval(nameMatcher.group(1), classStartPos, classEndPos));
                                        break; // 找到SimpleName后结束循环
                                    }
                                }
                            }
                            Matcher declarationMatcher = declarationPattern.matcher(line);
                            if (declarationMatcher.find()) {
                                // 如果匹配到了Method声明行，提取[startPos, endPos]
                                int startPos = Integer.parseInt(declarationMatcher.group(2));
                                int endPos = Integer.parseInt(declarationMatcher.group(3));
//                                System.out.println("Start Pos: " + startPos + ", End Pos: " + endPos);
                                int methodIndentation = getIndentation(line);//求方法声明行的行首有几个制表符
//                                System.out.println(line);
//                                System.out.println(methodIndentation);
                                // 一直往下读直到找到方法名
                                while (true) {
                                    line = br.readLine();
                                    if (line == null) {
                                        break; // 文件已经结束
                                    }
                                    int nameIndentation = getIndentation(line);//求行首有几个制表符
                                    if(nameIndentation != methodIndentation + 1)//方法名称行的缩进比方法声明行的缩进多1
                                        continue;
                                    Matcher nameMatcher = namePattern.matcher(line);
                                    if (nameMatcher.find()) {
                                        // 如果匹配到了SimpleName，提取methodName
                                        methodName = nameMatcher.group(1);
//                                        System.out.println(methodName);
                                        while (className.size() > 0 && className.get(className.size() - 1).getEndPos() < startPos)
                                            className.remove(className.size() - 1);
                                        String completeName = "";
                                        for(ClassNameAndInterval i:className) {
                                            completeName += i.getClassName() + "::";
                                        }
//                                        System.out.println("completeName = " + completeName);
//                                        System.out.println("methodName = " + methodName);
                                        intervalToMethodName.put(new Interval(startPos, endPos), completeName + methodName);
                                        break; // 找到SimpleName后结束循环
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    calculateASTScore(file.getAbsolutePath(), astScore, intervalToMethodName);//计算当前class的编辑脚本文件中各个方法的得分
                }
            }
        }

        return astScore;
    }

    public static int getIndentation(String line) {
        int tabCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                tabCount++;
            } else {
                break;
            }
        }
        return tabCount/4;
    }

    //计算一个编辑脚本的得分
    public static double calculateASTScore(String filePath, Map<String, Double> astScore, TreeMap<Interval, String> intervalToMethodName) {
//        for(Map.Entry<Interval, String> entry: intervalToMethodName.entrySet()) {
//            System.out.println(entry.getValue() + " = " + entry.getKey().toString());
//        }
        double totalScore = 0.0;
        int startPos = 0;
        int endPos = 0;
        String methodName = null;
        Pattern pattern = Pattern.compile("\\[(\\d+),(\\d+)\\]"); //用于匹配startPos和endPos的正则表达式

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = "";
            EditType currentEditType = null;
            int depth = 1;

            while ((line = reader.readLine()) != null) {
                //跳过开头第一行
                if (line.startsWith("===")) {
                    continue;
                }

                // 判断操作类型
                for (EditType type : EditType.values()) {
                    if (line.startsWith(type.name().toLowerCase().replace("_", "-"))) {
                        currentEditType = type;
                        break;
                    }
                }

                double nameChangeScore = 1;

                line = reader.readLine();line = reader.readLine();//向下跳过一行
//                //如果为更新操作，就需要判断当前是否是简单修改变量名或修饰符
//                if(currentEditType == EditType.UPDATE_NODE)
//                    nameChangeScore = isSimpleNameOrModifierChange(line) ? 0.01 : 1;

                if(line.contains("SimpleName:") || line.contains("Modifier:") || line.contains("LineComment"))
                    nameChangeScore = 0.01;


                Matcher matcher = pattern.matcher(line);//匹配编辑脚本中该操作的
                if (matcher.find()) {
                    startPos = Integer.parseInt(matcher.group(1));
                    endPos = Integer.parseInt(matcher.group(2));
//                    System.out.println("Start Pos: " + startPos + ", End Pos: " + endPos);
                    // 查找包含目标区间的项
                    Map.Entry<Interval, String> entry = intervalToMethodName.floorEntry(new Interval(startPos, endPos));
                    if (entry != null && entry.getKey().getStartPos() <= startPos && entry.getKey().getEndPos() >= endPos) {
//                            System.out.println("Interval found: " + entry.getValue());
                        methodName = entry.getValue();
                    }

                } else {
                    System.out.println("editscript,未找到该变更的位置匹配项");
                }
//                }
//                else {
//                    //找到“to”的下一行
//                    while(!line.equals("to"))
//                        line = reader.readLine();
//                    line = reader.readLine();
//                    Matcher matcher = pattern.matcher(line);
//                    if (matcher.find()) {
//                        startPos = Integer.parseInt(matcher.group(1));
//                        endPos = Integer.parseInt(matcher.group(2));
////                        System.out.println("Start Pos: " + startPos + ", End Pos: " + endPos);
//                        // 查找包含目标区间的项
//                        Map.Entry<Interval, String> entry = intervalToMethodName.floorEntry(new Interval(startPos, endPos));
//                        if (entry != null && entry.getKey().getStartPos() <= startPos && entry.getKey().getEndPos() >= endPos) {
////                            System.out.println("Interval found: " + entry.getValue());
//                            methodName = entry.getValue();
//                        }
//
//                    } else {
//                        System.out.println("editscript,未找到该变更的位置匹配项");
//                    }
//                }

                depth = 1;// 默认深度为1
                // 当读到“===”的行或者文件末尾时会停止往下读
                while (line != null && !line.startsWith("===")) {
                    line = reader.readLine();// 向下读一行
                    if(line == null) break;

                    if(currentEditType == EditType.INSERT_TREE || currentEditType == EditType.MOVE_TREE || currentEditType == EditType.DELETE_TREE)
                        depth = Math.max(depth, getIndentation(line) + 1);// 计算被修改AST子树的深度
                }
//                System.out.println("depth = " + depth);
                //计算当前操作的得分
                if (currentEditType != null) {
                    double operationScore = currentEditType.getWeight() * depth * nameChangeScore;
//                    System.out.println(operationScore);
//                    totalScore += operationScore;
//                    System.out.println(totalScore);
                    if(astScore.containsKey(methodName))
                        astScore.put(methodName, astScore.get(methodName) + operationScore);
                    else
                        astScore.put(methodName, operationScore);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalScore;
    }

    private static boolean isSimpleNameOrModifierChange(String line) {
        return line.contains("SimpleName:") || line.contains("Modifier:");
    }
}
