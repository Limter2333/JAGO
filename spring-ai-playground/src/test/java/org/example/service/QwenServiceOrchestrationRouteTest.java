package org.example.service;

import org.example.dto.ChatRequest;
import org.example.rag.dto.CitationPayload;
import org.example.rag.dto.RetrievalResponse;
import org.example.rag.service.RetrievalService;
import org.example.skill.CalculatorSkill;
import org.example.skill.DateTimeSkill;
import org.example.skill.EnterpriseSearchSkill;
import org.example.skill.TicketStatusSkill;
import org.example.skill.WeatherSkill;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QwenServiceOrchestrationRouteTest {

    @Test
    void orchestrationShouldRouteToRetrievalAndReturnCitations() {
        RetrievalService retrievalService = mock(RetrievalService.class);
        when(retrievalService.retrieve(any())).thenReturn(
                RetrievalResponse.builder()
                        .query("报销流程")
                        .topK(3)
                        .totalCandidates(2)
                        .citations(List.of(
                                CitationPayload.builder()
                                        .chunkId("a#0")
                                        .source("corpus/enterprise-handbook.txt")
                                        .content("报销流程需要审批")
                                        .score(0.98)
                                        .build()
                        ))
                        .build()
        );

        QwenService service = new StubMemoryQwenService(retrievalService, "MEMORY_PATH");
        ChatRequest request = new ChatRequest();
        request.setMessage("请从知识库回答报销流程");

        String result = service.chatWithOrchestration(request, List.of(new UserMessage("历史消息")));

        assertTrue(result.contains("知识库检索结果"));
        assertTrue(result.contains("corpus/enterprise-handbook.txt"));
    }

    @Test
    void orchestrationShouldRouteToToolByIntent() {
        QwenService service = new StubMemoryQwenService(mock(RetrievalService.class), "MEMORY_PATH");
        ChatRequest request = new ChatRequest();
        request.setMessage("帮我查询工单 INC-1001 状态");

        String result = service.chatWithOrchestration(request, List.of());

        assertTrue(result.contains("工单:INC-1001"));
        assertTrue(result.contains("归一化状态:"));
    }

    @Test
    void orchestrationShouldFallbackToMemoryPathWhenNoIntentMatched() {
        QwenService service = new StubMemoryQwenService(mock(RetrievalService.class), "MEMORY_PATH_CALLED");
        ChatRequest request = new ChatRequest();
        request.setMessage("你好，继续聊上次的话题");
        request.setEnableSkills(true);

        List<Message> history = List.of(new UserMessage("我们上次聊到请假"));
        String result = service.chatWithOrchestration(request, history);

        assertEquals("MEMORY_PATH_CALLED", result);
    }

    private static class StubMemoryQwenService extends QwenService {

        private final String memoryAnswer;

        StubMemoryQwenService(RetrievalService retrievalService, String memoryAnswer) {
            super(
                    null,
                    new WeatherSkill(),
                    new CalculatorSkill(),
                    new DateTimeSkill(),
                    new EnterpriseSearchSkill(),
                    new TicketStatusSkill(),
                    retrievalService
            );
            this.memoryAnswer = memoryAnswer;
        }

        @Override
        protected String callModelWithHistory(ChatRequest request, List<Message> history) {
            return memoryAnswer;
        }
    }
}
