package org.example.rag.controller;

import org.example.rag.dto.RetrievalRequest;
import org.example.rag.dto.RetrievalResponse;
import org.example.rag.service.RetrievalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kb/retrieval")
public class RetrievalController {

    private final RetrievalService retrievalService;

    public RetrievalController(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    /**
     * Retrieve top-k citations from indexed corpus chunks.
     */
    @PostMapping("/query")
    public RetrievalResponse query(@RequestBody RetrievalRequest request) {
        return retrievalService.retrieve(request);
    }
}
