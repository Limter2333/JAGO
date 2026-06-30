package org.example.rag.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CitationPayload {

    private String chunkId;

    private String source;

    private String content;

    private double score;
}
