package org.example.rag.loader;

import java.util.List;

public interface CorpusLoader {

    String load(String resourcePath);

    List<String> discoverDefaultResources();
}
