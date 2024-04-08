package com.zxc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplexityCalculator {
    private List<String> changedMethods;
    private Map<String, Integer> LOC;
    private Map<String, Integer> CC;
    private Map<String, Double> HV;
    private Map<String, Double> PCom;
    public List<String> getChangedMethods() {
        return changedMethods;
    }

    public void setChangedMethods(List<String> changedMethods) {
        this.changedMethods = changedMethods;
    }

    public Map<String, Integer> getLOC() {
        return LOC;
    }

    public void setLOC(Map<String, Integer> LOC) {
        this.LOC = LOC;
    }

    public Map<String, Integer> getCC() {
        return CC;
    }

    public void setCC(Map<String, Integer> CC) {
        this.CC = CC;
    }

    public Map<String, Double> getHV() {
        return HV;
    }

    public void setHV(Map<String, Double> hV) {
        HV = hV;
    }

    public Map<String, Double> getPCom() {
        return PCom;
    }

    public void setPCom(Map<String, Double> pCom) {
        PCom = pCom;
    }

    //获取两次commit之间发生变化的方法，以及这些方法的代码行数LOC和圈复杂度CC，并存入私有变量中
    public void getChangedMethods_LOC_CC(String gitDirectory, String oldCommit, String newCommit) {
        List<String> changedMethods = new ArrayList<>();
        Map<String, Integer> LOC = new HashMap<>();
        Map<String, Integer> CC = new HashMap<>();
        Map<String, Double> HV = new HashMap<>();
        Map<String, Double> PCom = new HashMap<>();
        try {
            // 创建 ProcessBuilder 对象来启动 Python 进程
            ProcessBuilder processBuilder = new ProcessBuilder("python",
                    "DeveloperContributionEvaluation/pythonScripts/getChangedMethods.py",
                    gitDirectory, oldCommit, newCommit);

            // 重定向错误流到标准输出流
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取 Python 进程的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int cnt = 0;
            String methodName = new String();
            while ((line = reader.readLine()) != null) {
                cnt++;
                if(cnt % 5 == 1) {
                    changedMethods.add(line);
                    methodName = line;
                } else if(cnt % 5 == 2) {
                    LOC.put(methodName, Integer.parseInt(line));
                } else if(cnt % 5 == 3){
                    CC.put(methodName, Integer.parseInt(line));
                } else if(cnt % 5 == 4){
                    HV.put(methodName, Double.parseDouble(line));
                } else {
                    PCom.put(methodName, Double.parseDouble(line));
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
        this.setChangedMethods(changedMethods);
        this.setLOC(LOC);
        this.setCC(CC);
        this.setHV(HV);
        this.setPCom(PCom);
    }
}
