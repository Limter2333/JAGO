package org.example.rag.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IngestionResponse {

    private int documentsSucceeded;

    private int chunksCreated;

    private int recordsWritten;

    private int totalIndexedRecords;

    private List<String> failedResources;
}
