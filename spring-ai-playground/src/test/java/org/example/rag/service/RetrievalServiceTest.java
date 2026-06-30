package org.example.rag.service;

import org.example.rag.dto.RetrievalRequest;
import org.example.rag.dto.RetrievalResponse;
import org.example.rag.embedder.EmbeddingService;
import org.example.rag.vector.InMemoryVectorIndex;
import org.example.rag.vector.VectorRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetrievalServiceTest {

    private InMemoryVectorIndex vectorIndex;

    @BeforeEach
    void setUp() {
        vectorIndex = new InMemoryVectorIndex();
        vectorIndex.upsertAll(List.of(
                VectorRecord.builder()
                        .id("doc-a#0")
                        .source("corpus/enterprise-handbook.txt")
                        .text("ticket workflow includes open in-progress resolved")
                        .embedding(new float[]{1.0f, 0.0f, 0.0f})
                        .build(),
                VectorRecord.builder()
                        .id("doc-b#0")
                        .source("corpus/security-policy.txt")
                        .text("security policy requires trace identifiers")
                        .embedding(new float[]{0.0f, 1.0f, 0.0f})
                        .build(),
                VectorRecord.builder()
                        .id("doc-c#0")
                        .source("corpus/faq.txt")
                        .text("general faq content")
                        .embedding(new float[]{0.0f, 0.0f, 1.0f})
                        .build()
        ));
    }

    @Test
    void retrieveShouldReturnDeterministicTopKWithCitations() {
        EmbeddingService embeddingService = text -> new float[]{1.0f, 0.0f, 0.0f};
        RetrievalService service = new RetrievalService(embeddingService, vectorIndex);

        RetrievalRequest request = new RetrievalRequest();
        request.setQuery("ticket workflow");
        request.setTopK(2);

        RetrievalResponse response = service.retrieve(request);

        assertEquals("ticket workflow", response.getQuery());
        assertEquals(2, response.getTopK());
        assertEquals(3, response.getTotalCandidates());
        assertEquals(2, response.getCitations().size());
        assertEquals("corpus/enterprise-handbook.txt", response.getCitations().get(0).getSource());
        assertTrue(response.getCitations().get(0).getScore() >= response.getCitations().get(1).getScore());
    }

    @Test
    void retrieveShouldRejectBlankQuery() {
        EmbeddingService embeddingService = text -> new float[]{1.0f, 0.0f, 0.0f};
        RetrievalService service = new RetrievalService(embeddingService, vectorIndex);

        RetrievalRequest request = new RetrievalRequest();
        request.setQuery(" ");

        boolean thrown = false;
        try {
            service.retrieve(request);
        } catch (IllegalArgumentException ex) {
            thrown = true;
            assertFalse(ex.getMessage().isBlank());
        }

        assertTrue(thrown);
    }
}
