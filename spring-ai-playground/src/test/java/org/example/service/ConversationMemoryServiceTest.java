package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversationMemoryServiceTest {

    private ConversationMemoryService service;

    @BeforeEach
    void setUp() {
        service = new ConversationMemoryService();
        ReflectionTestUtils.setField(service, "maxHistory", 3);
    }

    @Test
    void sameSessionShouldRetainContextAcrossTurns() {
        String sessionId = "session-a";
        service.appendMessage(sessionId, new UserMessage("hello"));
        service.appendMessage(sessionId, new AssistantMessage("hi"));

        assertEquals(2, service.getHistorySnapshot(sessionId).size());
        assertEquals(2, service.messageCount(sessionId));
    }

    @Test
    void crossSessionShouldNotLeakMessages() {
        String sessionA = "session-a";
        String sessionB = "session-b";

        service.appendMessage(sessionA, new UserMessage("a1"));
        service.appendMessage(sessionA, new AssistantMessage("a2"));
        service.appendMessage(sessionB, new UserMessage("b1"));

        assertEquals(2, service.getHistorySnapshot(sessionA).size());
        assertEquals(1, service.getHistorySnapshot(sessionB).size());
        assertNotEquals(service.getHistorySnapshot(sessionA), service.getHistorySnapshot(sessionB));
    }

    @Test
    void retentionPolicyShouldBeConfigurableAndTrimHistory() {
        String sessionId = "session-policy";
        service.appendMessage(sessionId, new UserMessage("m1"));
        service.appendMessage(sessionId, new AssistantMessage("m2"));
        service.appendMessage(sessionId, new UserMessage("m3"));
        service.appendMessage(sessionId, new AssistantMessage("m4"));

        assertEquals(3, service.messageCount(sessionId));

        service.updateMaxHistory(2);
        assertEquals(2, service.messageCount(sessionId));
        assertEquals(2, service.getMaxHistory());
    }

    @Test
    void retentionPolicyShouldRejectInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> service.updateMaxHistory(0));
    }
}
