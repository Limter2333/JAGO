package org.example.rag.controller;

import org.example.rag.dto.IngestionRequest;
import org.example.rag.dto.IngestionResponse;
import org.example.rag.service.DocumentIngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentIngestionController.class)
class DocumentIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentIngestionService documentIngestionService;

    @Test
    void ingestEndpointShouldReturnSummary() throws Exception {
        IngestionResponse response = IngestionResponse.builder()
                .documentsSucceeded(2)
                .chunksCreated(4)
                .recordsWritten(4)
                .totalIndexedRecords(4)
                .failedResources(List.of())
                .build();

        when(documentIngestionService.ingest(any(IngestionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/kb/ingestion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resources\":[\"corpus/enterprise-handbook.txt\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentsSucceeded").value(2))
                .andExpect(jsonPath("$.recordsWritten").value(4));
    }

    @Test
    void statsEndpointShouldExposeIndexedCount() throws Exception {
        when(documentIngestionService.indexStats())
                .thenReturn(Map.of("totalIndexedRecords", 6, "sourceCounts", Map.of("corpus/a.txt", 3)));

        mockMvc.perform(get("/kb/ingestion/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIndexedRecords").value(6));
    }
}
