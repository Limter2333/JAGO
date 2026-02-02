package org.example.dto;

import lombok.Data;

@Data
public class ChatRequest {
    // 用户输入的消息
    private String message;

    // 对话ID，用于区分不同会话（实现记忆功能）
    private String conversationId;

    // 可选：系统提示词（覆盖默认）
    private String systemPrompt;

    public ChatRequest(String message, String conversationId, String systemPrompt) {
        this.message = message;
        this.conversationId = conversationId;
        this.systemPrompt = systemPrompt;
    }
}