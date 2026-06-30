package org.example.rag.vector;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VectorRecord {

    private String id;

    private String source;

    private String text;

    private float[] embedding;
}
