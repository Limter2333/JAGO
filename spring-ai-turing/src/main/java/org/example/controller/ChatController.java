package org.example.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
//import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {
//
//    private final ChatClient chatClient;
//    private final EmbeddingClient embeddingClient;
//
//    // 注入 Spring AI Alibaba 提供的 ChatClient（自动绑定 qwen 模型）
//    public ChatController(ChatClient.Builder chatClientBuilder, EmbeddingClient embeddingClient) {
//        this.chatClient = chatClientBuilder.build();
//        this.embeddingClient = embeddingClient;
//    }
//
//    /**
//     * 简单文本生成：发送用户消息，返回 AI 回复（非流式）
//     */
//    @GetMapping("/chat")
//    public String chat(@RequestParam(value = "message", defaultValue = "你好") String message) {
//        String response = chatClient.prompt()
//                .user(message)
//                .call()
//                .content();
//        return response;
//    }
//
//    /**
//     * 带系统角色的对话（更可控）
//     */
//    @PostMapping("/chat/system")
//    public String chatWithSystem(@RequestBody ChatRequest request) {
//        String systemPrompt = request.getSystem() != null ? request.getSystem() : "你是一个乐于助人的AI助手。";
//        String userMessage = request.getMessage();
//
//        String response = chatClient.prompt()
//                .system(systemPrompt)
//                .user(userMessage)
//                .call()
//                .content();
//
//        return response;
//    }
//
//    /**
//     * 流式响应（SSE）——适合前端实时显示
//     */
//    @GetMapping("/chat/stream")
//    public void streamChat(@RequestParam String message, java.io.OutputStream os) throws Exception {
//        chatClient.prompt()
//                .user(message)
//                .stream()
//                .subscribe(chunk -> {
//                    try {
//                        os.write((chunk.getContent() + "\n").getBytes());
//                        os.flush();
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//    }
//
//    /**
//     * 文本嵌入（向量化）
//     */
//    @PostMapping("/embed")
//    public List<Double> embed(@RequestBody EmbedRequest request) {
//        return embeddingClient.embed(request.getText());
//    }
//
//    // 请求 DTO
//    public static class ChatRequest {
//        private String system;
//        private String message;
//
//        // getters & setters
//        public String getSystem() { return system; }
//        public void setSystem(String system) { this.system = system; }
//        public String getMessage() { return message; }
//        public void setMessage(String message) { this.message = message; }
//    }
//
//    public static class EmbedRequest {
//        private String text;
//
//        public String getText() { return text; }
//        public void setText(String text) { this.text = text; }
//    }
}
