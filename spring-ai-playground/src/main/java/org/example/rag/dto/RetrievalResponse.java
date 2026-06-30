package org.example.rag.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RetrievalResponse {

    private String query;

    private int topK;

    private int totalCandidates;

    private List<CitationPayload> citations;
}
