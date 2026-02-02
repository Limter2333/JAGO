// src/main/java/com/example/qwen/QwenApiDemo.java
package com.example.qwen;

import java.io.IOException;
import java.util.Scanner;

public class QwenApiDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 千问API调用DEMO ===");
        System.out.println("正在从配置文件加载API设置...");

        QwenApiClient client;
        try {
            client = new QwenApiClient(); // 不需要传入API密钥
        } catch (IOException e) {
            System.err.println("初始化API客户端失败: " + e.getMessage());
            return;
        }

        System.out.println("输入 'quit' 或 'exit' 退出程序");

        while (true) {
            System.out.print("\n请输入您的问题: ");
            String question = scanner.nextLine().trim();

            if ("quit".equalsIgnoreCase(question) || "exit".equalsIgnoreCase(question)) {
                System.out.println("程序已退出！");
                break;
            }

            if (question.isEmpty()) {
                System.out.println("问题不能为空，请重新输入！");
                continue;
            }

            try {
                System.out.println("正在获取答案...");

                long startTime = System.currentTimeMillis();
                String answer = client.sendQuestion(question);
                long endTime = System.currentTimeMillis();

                System.out.println("\n千问的回答:");
                System.out.println(answer);
                System.out.println("\n响应时间: " + (endTime - startTime) + " 毫秒");

            } catch (Exception e) {
                System.err.println("调用API时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}
