package org.example.rag.embedder;

public interface EmbeddingService {

    float[] embed(String text);
}
