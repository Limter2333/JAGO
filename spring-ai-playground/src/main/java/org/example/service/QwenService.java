package org.example.service;


import lombok.extern.slf4j.Slf4j;
import org.example.contract.CoreChatContract;
import org.example.dto.ChatRequest;
import org.example.dto.ChatResponse;
import org.example.rag.dto.CitationPayload;
import org.example.rag.dto.RetrievalRequest;
import org.example.rag.dto.RetrievalResponse;
import org.example.rag.service.RetrievalService;
import org.example.skill.CalculatorSkill;
import org.example.skill.DateTimeSkill;
import org.example.skill.EnterpriseSearchSkill;
import org.example.skill.TicketStatusSkill;
import org.example.skill.WeatherSkill;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class QwenService implements CoreChatContract {

        private static final String DEFAULT_MODEL = "kimi-k2-thinking";

        private final ChatClient chatClient;
        private final WeatherSkill weatherSkill;
        private final CalculatorSkill calculatorSkill;
        private final DateTimeSkill dateTimeSkill;
        private final EnterpriseSearchSkill enterpriseSearchSkill;
        private final TicketStatusSkill ticketStatusSkill;
        private final RetrievalService retrievalService;

        public QwenService(
                        ChatClient chatClient,
                        WeatherSkill weatherSkill,
                        CalculatorSkill calculatorSkill,
                        DateTimeSkill dateTimeSkill,
                        EnterpriseSearchSkill enterpriseSearchSkill,
                        TicketStatusSkill ticketStatusSkill,
                        RetrievalService retrievalService
        ) {
                this.chatClient = chatClient;
                this.weatherSkill = weatherSkill;
                this.calculatorSkill = calculatorSkill;
                this.dateTimeSkill = dateTimeSkill;
                this.enterpriseSearchSkill = enterpriseSearchSkill;
                this.ticketStatusSkill = ticketStatusSkill;
                this.retrievalService = retrievalService;
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
                .tools(weatherSkill, calculatorSkill, dateTimeSkill, enterpriseSearchSkill, ticketStatusSkill)
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
                return chatWithOrchestration(request, history);
        }

        /**
         * 统一编排路由：优先按意图选择 RAG 或工具，否则走记忆对话路径。
         */
        public String chatWithOrchestration(ChatRequest request, List<Message> history) {
                String message = request.getMessage() == null ? "" : request.getMessage().trim();

                if (isRetrievalIntent(request, message)) {
                        return routeRetrieval(message);
                }
                if (isTicketIntent(message)) {
                        return callTicketStatusTool(extractTicketId(message));
                }
                if (isWeatherIntent(message)) {
                        return callWeatherTool(extractCity(message));
                }
                if (isEnterpriseSearchIntent(message)) {
                        return callEnterpriseSearchTool(extractSearchTopic(message));
                }

                return callModelWithHistory(request, history);
        }

        protected String callModelWithHistory(ChatRequest request, List<Message> history) {
                ChatClient.ChatClientRequestSpec spec = chatClient.prompt().messages(history);

                if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
                        spec = spec.system(request.getSystemPrompt());
                }

                if (Boolean.FALSE.equals(request.getEnableSkills())) {
                        return spec.call().content();
                }

                return spec.tools(weatherSkill, calculatorSkill, dateTimeSkill, enterpriseSearchSkill, ticketStatusSkill)
                                .call()
                                .content();
        }

        private String routeRetrieval(String query) {
                RetrievalRequest retrievalRequest = new RetrievalRequest();
                retrievalRequest.setQuery(query);
                retrievalRequest.setTopK(3);

                RetrievalResponse response = retrievalService.retrieve(retrievalRequest);
                if (response.getCitations() == null || response.getCitations().isEmpty()) {
                        return "未检索到可用知识片段，请补充更多上下文。";
                }

                StringBuilder builder = new StringBuilder("知识库检索结果:\n");
                for (int i = 0; i < response.getCitations().size(); i++) {
                        CitationPayload citation = response.getCitations().get(i);
                        builder.append(i + 1)
                                .append(") [")
                                .append(citation.getSource())
                                .append("] ")
                                .append(citation.getContent())
                                .append("\n");
                }
                return builder.toString().trim();
        }

        private boolean isRetrievalIntent(ChatRequest request, String message) {
                if (Boolean.TRUE.equals(request.getEnableRag())) {
                        return true;
                }
                String lowered = message.toLowerCase();
                return lowered.contains("知识库")
                        || lowered.contains("文档")
                        || lowered.contains("流程")
                        || lowered.contains("policy")
                        || lowered.contains("handbook");
        }

        private boolean isWeatherIntent(String message) {
                return message.contains("天气") || message.toLowerCase().contains("weather");
        }

        private boolean isTicketIntent(String message) {
                return message.toLowerCase().contains("ticket")
                        || message.contains("工单")
                        || extractTicketId(message) != null;
        }

        private boolean isEnterpriseSearchIntent(String message) {
                return message.contains("政策")
                        || message.contains("规范")
                        || message.contains("流程")
                        || message.contains("报销")
                        || message.contains("请假");
        }

        private String extractCity(String message) {
                if (message == null || message.isBlank()) {
                        return "未知城市";
                }
                String[] cities = new String[]{"北京", "上海", "广州", "深圳"};
                for (String city : cities) {
                        if (message.contains(city)) {
                                return city;
                        }
                }
                return "未知城市";
        }

        private String extractSearchTopic(String message) {
                if (message == null || message.isBlank()) {
                        return "未知";
                }
                if (message.contains("报销")) {
                        return "报销流程";
                }
                if (message.contains("请假")) {
                        return "请假政策";
                }
                if (message.contains("安全")) {
                        return "安全规范";
                }
                return message.length() > 20 ? message.substring(0, 20) : message;
        }

        private String extractTicketId(String message) {
                if (message == null || message.isBlank()) {
                        return null;
                }
                Matcher matcher = Pattern.compile("[A-Za-z]{3}-\\d{4}").matcher(message);
                if (matcher.find()) {
                        return matcher.group().toUpperCase();
                }
                return null;
        }

    /**
     * 流式响应（使用 Skills）
     */
    public Flux<ChatResponse> streamChatWithSkills(ChatRequest request) {
        return chatClient.prompt()
                .user(request.getMessage())
                .tools(weatherSkill, calculatorSkill, dateTimeSkill, enterpriseSearchSkill, ticketStatusSkill)
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

        /**
         * Minimal explicit route for enterprise search tool execution with safe fallback.
         */
        public String callEnterpriseSearchTool(String topic) {
                try {
                        return enterpriseSearchSkill.searchInternalKnowledge(topic);
                } catch (RuntimeException ex) {
                        log.warn("Enterprise search tool failed. topic={}", topic, ex);
                        return "主题:未知;部门:未知;文档:未知;状态:服务不可用";
                }
        }

        /**
         * Minimal explicit route for ticket status tool execution with safe fallback.
         */
        public String callTicketStatusTool(String ticketId) {
                try {
                        return ticketStatusSkill.queryTicketStatus(ticketId);
                } catch (RuntimeException ex) {
                        log.warn("Ticket status tool failed. ticketId={}", ticketId, ex);
                        return "工单:未知工单;状态:UNKNOWN;归一化状态:服务不可用";
                }
        }
}
