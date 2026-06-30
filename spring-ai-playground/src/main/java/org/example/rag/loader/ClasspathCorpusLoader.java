package org.example.rag.loader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ClasspathCorpusLoader implements CorpusLoader {

    private static final String CORPUS_PATTERN = "classpath*:corpus/*.txt";

    @Override
    public String load(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (InputStream stream = resource.getInputStream()) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load corpus resource: " + resourcePath, ex);
        }
    }

    @Override
    public List<String> discoverDefaultResources() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(CORPUS_PATTERN);
            List<String> resolved = new ArrayList<>();
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null) {
                    resolved.add("corpus/" + filename);
                }
            }
            resolved.sort(Comparator.naturalOrder());
            return resolved;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to discover default corpus resources", ex);
        }
    }
}
