package com.zxc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CDG {


    public static void getCDG(String src, String newCommit, String projectName) {
        String basePath = "E:/IDEA/maven-project/DeveloperContributionEvaluation/PDGs";
        String folderName = newCommit.substring(0,6);
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

        // 构建命令
        List<String> command = new ArrayList<>();
        command.add("cmd");
        command.add("/c");
        command.add("E:/Postgraduate_study/joern-cli/joern-export");
        command.add(projectName + ".bin");
        command.add("--repr");
        command.add("cdg");
        command.add("--out");
        command.add("cdg");

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
                System.out.println("joern-export cdg执行成功");
            } else {
                System.out.println("joern-export cdg执行失败，exit code：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
