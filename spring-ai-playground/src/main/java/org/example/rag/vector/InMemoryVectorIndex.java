package org.example.rag.vector;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryVectorIndex {

    private final Map<String, VectorRecord> store = new ConcurrentHashMap<>();

    public int upsertAll(List<VectorRecord> records) {
        int count = 0;
        for (VectorRecord record : records) {
            store.put(record.getId(), record);
            count++;
        }
        return count;
    }

    public int count() {
        return store.size();
    }

    public Map<String, Integer> sourceCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (VectorRecord record : store.values()) {
            counts.merge(record.getSource(), 1, Integer::sum);
        }
        return counts;
    }

    public void clear() {
        store.clear();
    }

    public List<ScoredVectorRecord> topK(float[] queryEmbedding, int topK) {
        if (queryEmbedding == null || queryEmbedding.length == 0) {
            throw new IllegalArgumentException("queryEmbedding must not be empty");
        }
        int limit = Math.max(1, topK);

        return store.values().stream()
                .map(record -> ScoredVectorRecord.builder()
                        .record(record)
                        .score(cosineSimilarity(queryEmbedding, record.getEmbedding()))
                        .build())
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(float[] left, float[] right) {
        int size = Math.min(left.length, right.length);
        if (size == 0) {
            return 0.0;
        }

        double dot = 0.0;
        double leftNorm = 0.0;
        double rightNorm = 0.0;
        for (int i = 0; i < size; i++) {
            dot += left[i] * right[i];
            leftNorm += left[i] * left[i];
            rightNorm += right[i] * right[i];
        }
        if (leftNorm == 0.0 || rightNorm == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }
}
