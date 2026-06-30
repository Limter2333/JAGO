package org.example.rag.vector;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScoredVectorRecord {

    private VectorRecord record;

    private double score;
}
