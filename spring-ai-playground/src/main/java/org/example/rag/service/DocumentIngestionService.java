package org.example.rag.service;

import org.example.rag.chunker.TextChunker;
import org.example.rag.dto.IngestionRequest;
import org.example.rag.dto.IngestionResponse;
import org.example.rag.embedder.EmbeddingService;
import org.example.rag.loader.CorpusLoader;
import org.example.rag.vector.InMemoryVectorIndex;
import org.example.rag.vector.VectorRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService {

    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int DEFAULT_CHUNK_OVERLAP = 100;

    private final CorpusLoader corpusLoader;
    private final TextChunker textChunker;
    private final EmbeddingService embeddingService;
    private final InMemoryVectorIndex vectorIndex;

    public DocumentIngestionService(
            CorpusLoader corpusLoader,
            TextChunker textChunker,
            EmbeddingService embeddingService,
            InMemoryVectorIndex vectorIndex
    ) {
        this.corpusLoader = corpusLoader;
        this.textChunker = textChunker;
        this.embeddingService = embeddingService;
        this.vectorIndex = vectorIndex;
    }

    public IngestionResponse ingest(IngestionRequest request) {
        int chunkSize = request != null && request.getChunkSize() != null ? request.getChunkSize() : DEFAULT_CHUNK_SIZE;
        int chunkOverlap = request != null && request.getChunkOverlap() != null ? request.getChunkOverlap() : DEFAULT_CHUNK_OVERLAP;
        List<String> resources = resolveResources(request);

        List<VectorRecord> stagedRecords = new ArrayList<>();
        List<String> failedResources = new ArrayList<>();
        int documentsSucceeded = 0;

        for (String resource : resources) {
            try {
                String content = corpusLoader.load(resource);
                List<String> chunks = textChunker.chunk(content, chunkSize, chunkOverlap);
                for (int i = 0; i < chunks.size(); i++) {
                    String chunk = chunks.get(i);
                    float[] embedding = embeddingService.embed(chunk);
                    stagedRecords.add(VectorRecord.builder()
                            .id(resource + "#" + i)
                            .source(resource)
                            .text(chunk)
                            .embedding(embedding)
                            .build());
                }
                documentsSucceeded++;
            } catch (RuntimeException ex) {
                failedResources.add(resource);
            }
        }

        int recordsWritten = vectorIndex.upsertAll(stagedRecords);
        return IngestionResponse.builder()
                .documentsSucceeded(documentsSucceeded)
                .chunksCreated(stagedRecords.size())
                .recordsWritten(recordsWritten)
                .totalIndexedRecords(vectorIndex.count())
                .failedResources(failedResources)
                .build();
    }

    public Map<String, Object> indexStats() {
        return Map.of(
                "totalIndexedRecords", vectorIndex.count(),
                "sourceCounts", vectorIndex.sourceCounts()
        );
    }

    public void clearIndex() {
        vectorIndex.clear();
    }

    private List<String> resolveResources(IngestionRequest request) {
        if (request != null && request.getResources() != null && !request.getResources().isEmpty()) {
            return request.getResources();
        }
        return corpusLoader.discoverDefaultResources();
    }
}
