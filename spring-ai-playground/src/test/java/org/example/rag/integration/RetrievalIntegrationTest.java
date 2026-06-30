package org.example.rag.integration;

import org.example.rag.dto.IngestionRequest;
import org.example.rag.dto.IngestionResponse;
import org.example.rag.dto.RetrievalRequest;
import org.example.rag.dto.RetrievalResponse;
import org.example.rag.service.DocumentIngestionService;
import org.example.rag.service.RetrievalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
    "spring.ai.dashscope.api-key=test-key"
})
class RetrievalIntegrationTest {

    @Autowired
    private DocumentIngestionService documentIngestionService;

    @Autowired
    private RetrievalService retrievalService;

    @BeforeEach
    void resetIndex() {
        documentIngestionService.clearIndex();
    }

    @Test
    void retrievalShouldReturnCitationFromIngestedDeterministicFixture() {
        IngestionRequest ingestionRequest = new IngestionRequest();
        ingestionRequest.setResources(List.of("corpus/enterprise-handbook.txt", "corpus/security-policy.txt"));
        ingestionRequest.setChunkSize(500);
        ingestionRequest.setChunkOverlap(100);

        IngestionResponse ingestionResponse = documentIngestionService.ingest(ingestionRequest);
        assertEquals(2, ingestionResponse.getDocumentsSucceeded());
        assertTrue(ingestionResponse.getRecordsWritten() > 0);

        RetrievalRequest retrievalRequest = new RetrievalRequest();
        retrievalRequest.setQuery("ticket workflow state");
        retrievalRequest.setTopK(3);

        RetrievalResponse retrievalResponse = retrievalService.retrieve(retrievalRequest);

        assertEquals(3, retrievalResponse.getTopK());
        assertTrue(retrievalResponse.getTotalCandidates() >= 2);
        assertFalse(retrievalResponse.getCitations().isEmpty());
        assertTrue(retrievalResponse.getCitations().stream()
                .map(c -> c.getSource())
                .anyMatch(source -> source.equals("corpus/enterprise-handbook.txt")));
    }
}
