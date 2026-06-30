package org.example.rag.service;

import org.example.rag.dto.CitationPayload;
import org.example.rag.dto.RetrievalRequest;
import org.example.rag.dto.RetrievalResponse;
import org.example.rag.embedder.EmbeddingService;
import org.example.rag.vector.InMemoryVectorIndex;
import org.example.rag.vector.ScoredVectorRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrievalService {

    private static final int DEFAULT_TOP_K = 5;

    private final EmbeddingService embeddingService;
    private final InMemoryVectorIndex vectorIndex;

    public RetrievalService(EmbeddingService embeddingService, InMemoryVectorIndex vectorIndex) {
        this.embeddingService = embeddingService;
        this.vectorIndex = vectorIndex;
    }

    public RetrievalResponse retrieve(RetrievalRequest request) {
        if (request == null || request.getQuery() == null || request.getQuery().isBlank()) {
            throw new IllegalArgumentException("query must not be blank");
        }

        int topK = request.getTopK() != null ? request.getTopK() : DEFAULT_TOP_K;
        float[] queryEmbedding = embeddingService.embed(request.getQuery());
        List<ScoredVectorRecord> scoredRecords = vectorIndex.topK(queryEmbedding, topK);

        List<CitationPayload> citations = scoredRecords.stream()
                .map(item -> CitationPayload.builder()
                        .chunkId(item.getRecord().getId())
                        .source(item.getRecord().getSource())
                        .content(item.getRecord().getText())
                        .score(item.getScore())
                        .build())
                .toList();

        return RetrievalResponse.builder()
                .query(request.getQuery())
                .topK(topK)
                .totalCandidates(vectorIndex.count())
                .citations(citations)
                .build();
    }
}
