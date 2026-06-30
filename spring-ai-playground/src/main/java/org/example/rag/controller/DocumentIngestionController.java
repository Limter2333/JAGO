package org.example.rag.controller;

import org.example.rag.dto.IngestionRequest;
import org.example.rag.dto.IngestionResponse;
import org.example.rag.service.DocumentIngestionService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/kb/ingestion")
public class DocumentIngestionController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentIngestionController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    /**
     * Trigger corpus ingestion into the in-memory vector index.
     */
    @PostMapping
    public IngestionResponse ingest(@RequestBody(required = false) IngestionRequest request) {
        return documentIngestionService.ingest(request);
    }

    /**
     * Return current vector index statistics for ingestion verification.
     */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return documentIngestionService.indexStats();
    }

    /**
     * Clear all indexed records to reset ingestion state.
     */
    @DeleteMapping
    public Map<String, String> clear() {
        documentIngestionService.clearIndex();
        return Map.of("status", "cleared");
    }
}
