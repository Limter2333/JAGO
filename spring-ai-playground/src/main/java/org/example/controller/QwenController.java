package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ChatRequest;
import org.example.dto.ChatResponse;
import org.example.service.ConversationMemoryService;
import org.example.service.QwenService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/ai")
public class QwenController {

    private final ChatClient chatClient;

    private final QwenService qwenService;

    private final ConversationMemoryService conversationMemoryService;

    public QwenController(
            ChatClient.Builder chatClient,
            QwenService qwenService,
            ConversationMemoryService conversationMemoryService
    ) {
        this.chatClient = chatClient.build();
        this.qwenService = qwenService;
        this.conversationMemoryService = conversationMemoryService;
    }

    @GetMapping(value = "/chat")
    public String chat(@RequestParam(value =  "input") String input) {
        return this.chatClient.prompt()
                .user(input)
                .call()
                .content();
    }



    /**
     * 同步对话（带上下文记忆）
     */
    @PostMapping("/sync")
    public ChatResponse chatSync(@RequestBody ChatRequest request) {
        // 添加有效的日志记录
        log.debug("Received message. conversationId={}, userId={}", request.getConversationId(), request.getUserId());

        String conversationId = conversationMemoryService.resolveConversationId(request.getConversationId());
        request.setConversationId(conversationId);

        // 添加用户消息
        conversationMemoryService.appendMessage(conversationId, new UserMessage(request.getMessage()));

        List<Message> history = conversationMemoryService.getHistorySnapshot(conversationId);
        String content = qwenService.chatWithMemory(request, history);

        // 添加助手回复到历史
        conversationMemoryService.appendMessage(conversationId, new AssistantMessage(content));

        return ChatResponse.builder()
            .content(content)
            .conversationId(conversationId)
            .model("kimi-k2-thinking")
            .timestamp(System.currentTimeMillis())
            .success(true)
            .traceId(UUID.randomUUID().toString())
            .build();
    }

    /**
     * 流式对话（不带记忆，简化版）
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(
            @RequestParam String message,
            @RequestParam(required = false) String conversationId) {

        return chatClient.prompt()
                .user(message)
                .stream()
                .content()
                .map(chunk -> "data: " + chunk + "\n\n")
                .concatWith(Flux.just("data: [DONE]\n\n"));
    }

    /**
     * 清除对话记忆
     */
    @DeleteMapping("/memory/{conversationId}")
    public String clearMemory(@PathVariable String conversationId) {
        conversationMemoryService.clearMemory(conversationId);
        return "Conversation memory cleared: " + conversationId;
    }

    @GetMapping("/test-log")
    public String testLog() {
        // 使用与 xml 中配置的完全相同的 logger 名
        org.slf4j.Logger httpLogger = org.slf4j.LoggerFactory.getLogger(
                "org.example.config.HttpLoggingConfig"
        );

        httpLogger.debug("========== 测试 HTTP 日志 ==========");
        httpLogger.debug("这是一条测试消息");
        httpLogger.info("如果看到这条，说明 info 级别能写入");

        return "日志已打印，请检查 http-request.log";
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return qwenService.chat(request);
    }

    @PostMapping("/chat/skills")
    public ChatResponse chatWithSkills(@RequestBody ChatRequest request) {
        return qwenService.chatWithSkills(request);
    }

    @PostMapping(value = "/chat/skills/stream", produces = "text/event-stream")
    public Flux<ChatResponse> streamChatWithSkills(@RequestBody ChatRequest request) {
        return qwenService.streamChatWithSkills(request);
    }
}
