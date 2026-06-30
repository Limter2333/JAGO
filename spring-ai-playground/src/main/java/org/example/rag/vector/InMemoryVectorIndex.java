package org.example.rag.vector;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
}
