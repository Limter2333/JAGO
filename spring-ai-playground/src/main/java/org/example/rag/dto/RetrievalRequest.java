package org.example.rag.dto;

import lombok.Data;

@Data
public class RetrievalRequest {

    private String query;

    private Integer topK;
}
