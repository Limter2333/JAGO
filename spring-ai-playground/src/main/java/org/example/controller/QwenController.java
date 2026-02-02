package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ChatRequest;
import org.example.dto.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/ai")
public class QwenController {

    private final ChatClient chatClient;

    public QwenController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    // 简单的内存存储： conversationId -> 消息列表
    private final Map<String, List<Message>> conversationStore = new ConcurrentHashMap<>();

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
        log.debug("Received message: {}", request.getMessage());


        String conversationId = request.getConversationId() != null ?
                request.getConversationId() : UUID.randomUUID().toString();

        // 获取或创建对话历史
        List<Message> history = conversationStore.computeIfAbsent(conversationId, k -> new ArrayList<>());

        // 添加用户消息
        history.add(new UserMessage(request.getMessage()));

        // 调用模型（携带历史上下文）
        String content = chatClient.prompt()
                .messages(history)  // 传入完整历史
                .call()
                .content();

        // 添加助手回复到历史
        history.add(new AssistantMessage(content));

        // 可选：限制历史长度，防止 Token 超限
        if (history.size() > 20) {
            history = history.subList(history.size() - 20, history.size());
            conversationStore.put(conversationId, history);
        }

        return new ChatResponse(content, conversationId, "kimi-k2-thinking");
    }

    /**
     * 流式对话（不带记忆，简化版）
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(
            @RequestParam String message,
            @RequestParam(required = false) String conversationId) {

        String convId = conversationId != null ? conversationId : UUID.randomUUID().toString();

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
        conversationStore.remove(conversationId);
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
}
