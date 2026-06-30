package org.example.rag.service;

import org.example.rag.chunker.SimpleTextChunker;
import org.example.rag.chunker.TextChunker;
import org.example.rag.dto.IngestionRequest;
import org.example.rag.dto.IngestionResponse;
import org.example.rag.embedder.EmbeddingService;
import org.example.rag.loader.CorpusLoader;
import org.example.rag.vector.InMemoryVectorIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentIngestionServiceTest {

    private InMemoryVectorIndex vectorIndex;
    private TextChunker textChunker;

    @BeforeEach
    void setUp() {
        this.vectorIndex = new InMemoryVectorIndex();
        this.textChunker = new SimpleTextChunker();
    }

    @Test
    void ingestShouldProcessSampleCorpusAndWriteIndex() {
        CorpusLoader loader = new StubCorpusLoader();
        EmbeddingService embedder = text -> new float[]{1.0f, 0.0f};
        DocumentIngestionService service = new DocumentIngestionService(loader, textChunker, embedder, vectorIndex);

        IngestionResponse response = service.ingest(new IngestionRequest());

        assertEquals(2, response.getDocumentsSucceeded());
        assertTrue(response.getChunksCreated() > 0);
        assertEquals(response.getChunksCreated(), response.getRecordsWritten());
        assertEquals(response.getRecordsWritten(), response.getTotalIndexedRecords());
        assertTrue(response.getFailedResources().isEmpty());
    }

    @Test
    void ingestShouldReportFailureWhenResourceCannotBeLoaded() {
        CorpusLoader loader = new FailingCorpusLoader();
        EmbeddingService embedder = text -> new float[]{1.0f, 0.0f};
        DocumentIngestionService service = new DocumentIngestionService(loader, textChunker, embedder, vectorIndex);

        IngestionRequest request = new IngestionRequest();
        request.setResources(List.of("corpus/missing.txt"));

        IngestionResponse response = service.ingest(request);

        assertEquals(0, response.getDocumentsSucceeded());
        assertEquals(0, response.getRecordsWritten());
        assertEquals(1, response.getFailedResources().size());
        assertEquals("corpus/missing.txt", response.getFailedResources().get(0));
    }

    private static class StubCorpusLoader implements CorpusLoader {

        @Override
        public String load(String resourcePath) {
            return "money tree policy and process handbook";
        }

        @Override
        public List<String> discoverDefaultResources() {
            return List.of("corpus/enterprise-handbook.txt", "corpus/security-policy.txt");
        }
    }

    private static class FailingCorpusLoader implements CorpusLoader {

        @Override
        public String load(String resourcePath) {
            throw new IllegalStateException("cannot load resource");
        }

        @Override
        public List<String> discoverDefaultResources() {
            return List.of("corpus/missing.txt");
        }
    }
}
