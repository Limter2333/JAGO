package org.example.rag.embedder;

import org.springframework.stereotype.Component;

@Component
public class HashEmbeddingService implements EmbeddingService {

    private static final int EMBEDDING_DIMENSION = 32;

    @Override
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }

        float[] vector = new float[EMBEDDING_DIMENSION];
        String[] tokens = text.toLowerCase().split("\\s+");
        for (String token : tokens) {
            int hash = token.hashCode();
            int idx = Math.floorMod(hash, EMBEDDING_DIMENSION);
            vector[idx] += 1.0f;
        }
        return normalize(vector);
    }

    private float[] normalize(float[] vector) {
        double sumSquares = 0.0;
        for (float value : vector) {
            sumSquares += value * value;
        }
        if (sumSquares == 0.0) {
            return vector;
        }
        double norm = Math.sqrt(sumSquares);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (vector[i] / norm);
        }
        return vector;
    }
}
