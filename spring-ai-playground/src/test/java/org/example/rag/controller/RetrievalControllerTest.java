package org.example.rag.controller;

import org.example.rag.dto.CitationPayload;
import org.example.rag.dto.RetrievalResponse;
import org.example.rag.service.RetrievalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RetrievalController.class)
class RetrievalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetrievalService retrievalService;

    @Test
    void queryEndpointShouldReturnTopKCitations() throws Exception {
        RetrievalResponse response = RetrievalResponse.builder()
                .query("ticket workflow")
                .topK(2)
                .totalCandidates(5)
                .citations(List.of(
                        CitationPayload.builder()
                                .chunkId("doc-a#0")
                                .source("corpus/enterprise-handbook.txt")
                                .content("ticket workflow includes open in-progress resolved")
                                .score(0.99)
                                .build()
                ))
                .build();

        when(retrievalService.retrieve(any())).thenReturn(response);

        mockMvc.perform(post("/kb/retrieval/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"ticket workflow\",\"topK\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topK").value(2))
                .andExpect(jsonPath("$.citations[0].source").value("corpus/enterprise-handbook.txt"));
    }
}
