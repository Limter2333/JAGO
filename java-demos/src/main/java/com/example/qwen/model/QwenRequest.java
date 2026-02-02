// src/main/java/com/example/qwen/model/QwenRequest.java
package com.example.qwen.model;

public class QwenRequest {
    private String model;
    private Messages[] messages;

    public static class Messages {
        private String role;
        private String content;

        public Messages(String role, String content) {
            this.role = role;
            this.content = content;
        }

        // Getters and setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    // Getters and setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Messages[] getMessages() { return messages; }
    public void setMessages(Messages[] messages) { this.messages = messages; }
}
