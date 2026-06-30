package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationMemoryService {

    private final Map<String, List<org.springframework.ai.chat.messages.Message>> conversationStore = new ConcurrentHashMap<>();

    @Value("${money-tree.memory.max-history:20}")
    private int maxHistory;

    public String resolveConversationId(String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return conversationId;
    }

    public List<org.springframework.ai.chat.messages.Message> getHistorySnapshot(String conversationId) {
        List<org.springframework.ai.chat.messages.Message> messages = conversationStore.computeIfAbsent(
                conversationId,
                key -> new ArrayList<>()
        );
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }

    public void appendMessage(String conversationId, org.springframework.ai.chat.messages.Message message) {
        List<org.springframework.ai.chat.messages.Message> messages = conversationStore.computeIfAbsent(
                conversationId,
                key -> new ArrayList<>()
        );

        synchronized (messages) {
            messages.add(message);
            trimIfNeeded(messages);
        }
    }

    public void clearMemory(String conversationId) {
        conversationStore.remove(conversationId);
    }

    private void trimIfNeeded(List<org.springframework.ai.chat.messages.Message> messages) {
        if (messages.size() <= maxHistory) {
            return;
        }
        int fromIndex = messages.size() - maxHistory;
        List<org.springframework.ai.chat.messages.Message> trimmed = new ArrayList<>(
                messages.subList(fromIndex, messages.size())
        );
        messages.clear();
        messages.addAll(trimmed);
    }
}