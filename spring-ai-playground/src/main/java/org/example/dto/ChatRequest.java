package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    // 用户输入的消息
    private String message;

    // 用户ID，用于后续多用户隔离与审计
    private String userId;

    // 对话ID，用于区分不同会话（实现记忆功能）
    private String conversationId;

    // 可选：系统提示词（覆盖默认）
    private String systemPrompt;

    // 是否启用工具调用（默认 true）
    private Boolean enableSkills;

    // 是否启用检索增强（预留给 RAG 阶段）
    private Boolean enableRag;
}