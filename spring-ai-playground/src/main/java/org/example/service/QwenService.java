package org.example.service;


import org.example.dto.ChatRequest;
import org.example.dto.ChatResponse;
import org.example.skill.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class QwenService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private WeatherSkill weatherSkill;

    @Autowired
    private CalculatorSkill calculatorSkill;

    @Autowired
    private DateTimeSkill dateTimeSkill;

    /**
     * 普通对话（不使用 Skills）
     */
    public ChatResponse chat(ChatRequest request) {
        String reply = chatClient.prompt()
                .user(request.getMessage())
                .call()
                .content();

        return ChatResponse.builder()
                .content(reply)
                .build();
    }

    /**
     * 使用 Skills 的对话
     */
    public ChatResponse chatWithSkills(ChatRequest request) {
        String reply = chatClient.prompt()
                .user(request.getMessage())
                .tools(weatherSkill, calculatorSkill, dateTimeSkill)
                .call()
                .content();

        return ChatResponse.builder()
                .content(reply)
                .build();
    }

    /**
     * 流式响应（使用 Skills）
     */
    public Flux<ChatResponse> streamChatWithSkills(ChatRequest request) {
        return chatClient.prompt()
                .user(request.getMessage())
                .tools(weatherSkill, calculatorSkill, dateTimeSkill)
                .stream()
                .content()
                .map(content -> ChatResponse.builder()
                        .content(content)
                        .build());
    }
}
