package org.example.rag.chunker;

import java.util.List;

public interface TextChunker {

    List<String> chunk(String text, int chunkSize, int overlap);
}
