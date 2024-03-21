package com.zxc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

    // 计算文件夹下所有编辑脚本得分的总和
    public static double calculateTotalASTScore(String folderPath) {

        File folder = new File(folderPath);
        if(!folder.exists()) {
            //新增文件或删除文件的得分有待补充
            return 0;
        }

        File[] files = folder.listFiles(); // 获取文件夹下的所有文件
        double totalScore = 0.0;

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) { // 确保是文件而不是文件夹
                    totalScore += calculateASTScore(file.getAbsolutePath());
                }
            }
        }

        return totalScore;
    }

    //计算一个编辑脚本的得分
    public static double calculateASTScore(String filePath) {
        double totalScore = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = "";
            EditType currentEditType = null;
            int depth = 1;
//            while ((line = reader.readLine()) != null) {
//                while(line!=null) {
//                    line= reader.readLine();
//                    System.out.println(line);
//                }
//                System.out.println(line);
//                if(line == null) break;
//            }

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
//                System.out.println(currentEditType);
                //如果为更新操作，就需要判断当前是否是简单修改变量名或修饰符
                if(currentEditType == EditType.UPDATE_NODE){
                    line = reader.readLine();line = reader.readLine();//向下跳过两行
                    nameChangeScore = isSimpleNameOrModifierChange(line) ? 0.01 : 1;
                }

                depth = 1;// 默认深度为1
                // 当读到“===”的行或者文件末尾时会停止往下读
                while (line != null && !line.startsWith("===")) {
                    line = reader.readLine();// 向下读一行
                    if(line == null) break;
                    if(line.startsWith("at ")){
                        // 使用正则表达式匹配数字部分。\\D表示非数字字符，replaceAll将非数字字符替换为空，仅留下数字
                        String number = line.replaceAll("\\D+", "");
                        // 将提取的数字字符串转换为int类型
                        depth = Integer.parseInt(number) + 1;// 计算被修改AST子树的深度
//                        System.out.println("depth = " + depth);
                    }
                }

                //计算当前操作的得分
                if (currentEditType != null) {
                    double operationScore = currentEditType.getWeight() * depth * nameChangeScore;
//                    System.out.println(operationScore);
                    totalScore += operationScore;
//                    System.out.println(totalScore);
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
