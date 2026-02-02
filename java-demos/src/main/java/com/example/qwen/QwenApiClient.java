// src/main/java/com/example/qwen/QwenApiClient.java
package com.example.qwen;

import com.example.qwen.model.QwenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class QwenApiClient {
    private final String apiKey;
    private final String apiUrl;
    private final String model;
    private final ObjectMapper objectMapper;

    public QwenApiClient() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("找不到配置文件 application.properties");
            }
            props.load(input);
        }

        this.apiKey = props.getProperty("qwen.api.key");
        this.apiUrl = props.getProperty("qwen.api.url");
        this.model = props.getProperty("qwen.model", "qwen-max"); // 默认值
        this.objectMapper = new ObjectMapper();

        if (this.apiKey == null || this.apiKey.trim().isEmpty()) {
            throw new IOException("配置文件中未找到API密钥，请检查 qwen.api.key 配置项");
        }
    }

    // 保留原有的sendQuestion方法，但使用配置的model
    public String sendQuestion(String question) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Qwen-Java-Client/1.0");

        connection.setDoOutput(true);

        QwenRequest request = new QwenRequest();
        request.setModel(model); // 使用配置文件中的模型

        QwenRequest.Messages[] messages = {
            new QwenRequest.Messages("user", question)
        };

        request.setMessages(messages);

        String requestBody = objectMapper.writeValueAsString(request);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        String responseMessage = connection.getResponseMessage();
        String responseMessageAfterFormat = formatAsJson(responseMessage);
        System.out.println(responseMessageAfterFormat);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP error code: " + responseCode +
                ", Error: " + getErrorString(connection));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        connection.disconnect();

        return parseResponse(response.toString());
    }

    // 保留原有的辅助方法
    private String parseResponse(String jsonResponse) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode choicesNode = rootNode.path("output").path("choices");

        if (choicesNode.isArray() && choicesNode.size() > 0) {
            return choicesNode.get(0).path("message").path("content").asText();
        } else {
            throw new RuntimeException("无法解析API响应: " + jsonResponse);
        }
    }

    private String getErrorString(HttpURLConnection connection) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()))) {
            StringBuilder error = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                error.append(responseLine.trim());
            }
            return error.toString();
        } catch (IOException e) {
            return "未知错误";
        }
    }

    /**
     * 将响应字符串格式化为JSON格式
     */
    private String formatAsJson(String response) {
        if (response == null || response.trim().isEmpty()) {
            return "{}";
        }

        // 如果已经是JSON格式则直接返回
        if (response.trim().startsWith("{") || response.trim().startsWith("[")) {
            return response;
        }

        // 否则包装为JSON对象
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            // 如果格式化失败，返回原始内容的JSON转义版本
            return "\"" + response.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
        }
    }
}
