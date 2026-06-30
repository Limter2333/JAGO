package org.example.service;


import org.example.contract.CoreChatContract;
import org.example.dto.ChatRequest;
import org.example.dto.ChatResponse;
import org.example.skill.CalculatorSkill;
import org.example.skill.DateTimeSkill;
import org.example.skill.WeatherSkill;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Service
public class QwenService implements CoreChatContract {

        private static final String DEFAULT_MODEL = "kimi-k2-thinking";

        private final ChatClient chatClient;
        private final WeatherSkill weatherSkill;
        private final CalculatorSkill calculatorSkill;
        private final DateTimeSkill dateTimeSkill;

        public QwenService(
                        ChatClient chatClient,
                        WeatherSkill weatherSkill,
                        CalculatorSkill calculatorSkill,
                        DateTimeSkill dateTimeSkill
        ) {
                this.chatClient = chatClient;
                this.weatherSkill = weatherSkill;
                this.calculatorSkill = calculatorSkill;
                this.dateTimeSkill = dateTimeSkill;
        }

    /**
     * 普通对话（不使用 Skills）
     */
        @Override
    public ChatResponse chat(ChatRequest request) {
        String reply = chatClient.prompt()
                .user(request.getMessage())
                .call()
                .content();

        return ChatResponse.builder()
                .content(reply)
                .conversationId(request.getConversationId())
                .model(DEFAULT_MODEL)
                .timestamp(System.currentTimeMillis())
                .success(true)
                .traceId(UUID.randomUUID().toString())
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
                                .conversationId(request.getConversationId())
                                .model(DEFAULT_MODEL)
                                .timestamp(System.currentTimeMillis())
                                .success(true)
                                .traceId(UUID.randomUUID().toString())
                .build();
    }

        /**
         * 使用会话历史并按请求配置选择是否启用 Skills。
         */
        public String chatWithMemory(ChatRequest request, List<Message> history) {
                ChatClient.ChatClientRequestSpec spec = chatClient.prompt().messages(history);

                if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
                        spec = spec.system(request.getSystemPrompt());
                }

                if (Boolean.FALSE.equals(request.getEnableSkills())) {
                        return spec.call().content();
                }

                return spec.tools(weatherSkill, calculatorSkill, dateTimeSkill)
                                .call()
                                .content();
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
                        .conversationId(request.getConversationId())
                        .model(DEFAULT_MODEL)
                        .timestamp(System.currentTimeMillis())
                        .success(true)
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }

        /**
         * Minimal explicit route for weather tool execution with arg validation.
         */
        public String callWeatherTool(String city) {
                return weatherSkill.queryWeather(city);
        }
}
