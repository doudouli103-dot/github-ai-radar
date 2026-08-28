package com.github.airadar.analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.airadar.analysis.model.RepositoryAnalysis;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RepositoryAnalysisParser {

    private final ObjectMapper objectMapper;

    public RepositoryAnalysisParser() {
        this(new ObjectMapper());
    }

    public RepositoryAnalysisParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RepositoryAnalysis parse(Long repositoryId, LocalDate analysisDate, String analysisType,
                                    String modelRole, String rawContent) {
        String content = rawContent == null ? "" : rawContent.trim();
        try {
            JsonNode root = objectMapper.readTree(content);
            return new RepositoryAnalysis(repositoryId, analysisDate, analysisType, modelRole,
                    text(root, "category", "Unknown"),
                    summary(root),
                    text(root, "growthReason", ""),
                    text(root, "technicalInnovation", ""),
                    text(root, "businessValue", ""),
                    learningValue(root),
                    keyFiles(root),
                    content);
        } catch (IOException ex) {
            return fallback(repositoryId, analysisDate, analysisType, modelRole, content);
        }
    }

    private RepositoryAnalysis fallback(Long repositoryId, LocalDate analysisDate, String analysisType,
                                        String modelRole, String content) {
        return new RepositoryAnalysis(repositoryId, analysisDate, analysisType, modelRole,
                "Unknown", content, content, content, content, content,
                Collections.<String>emptyList(), "{\"content\":\"" + escape(content) + "\"}");
    }

    private String summary(JsonNode root) {
        String architectureSummary = text(root, "architectureSummary", "");
        if (!architectureSummary.isEmpty()) {
            return architectureSummary;
        }
        return text(root, "summary", "");
    }

    private String learningValue(JsonNode root) {
        String learningValue = text(root, "learningValue", "");
        if (!learningValue.isEmpty()) {
            return learningValue;
        }
        JsonNode suggestions = root.get("learningSuggestions");
        if (suggestions == null || !suggestions.isArray()) {
            return "";
        }
        List<String> values = new ArrayList<String>();
        for (JsonNode suggestion : suggestions) {
            values.add(suggestion.asText());
        }
        return String.join("; ", values);
    }

    private List<String> keyFiles(JsonNode root) {
        JsonNode keyFiles = root.get("keyFiles");
        if (keyFiles == null || !keyFiles.isArray()) {
            return Collections.emptyList();
        }
        List<String> paths = new ArrayList<String>();
        for (JsonNode keyFile : keyFiles) {
            JsonNode path = keyFile.get("path");
            if (path != null && !path.asText().trim().isEmpty()) {
                paths.add(path.asText());
            }
        }
        return paths;
    }

    private String text(JsonNode root, String field, String defaultValue) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        return node.asText(defaultValue);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
