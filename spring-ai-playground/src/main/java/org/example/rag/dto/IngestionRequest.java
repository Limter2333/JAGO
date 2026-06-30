package org.example.rag.dto;

import lombok.Data;

import java.util.List;

@Data
public class IngestionRequest {

    private List<String> resources;

    private Integer chunkSize;

    private Integer chunkOverlap;
}
