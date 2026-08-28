package com.github.airadar.analysis.model;

import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

public class RepositoryAnalysis {

    private final Long repositoryId;
    private final LocalDate analysisDate;
    private final String analysisType;
    private final String modelRole;
    private final String category;
    private final String summary;
    private final String growthReason;
    private final String technicalInnovation;
    private final String businessValue;
    private final String learningValue;
    private final List<String> keyFiles;
    private final String rawOutput;

    public RepositoryAnalysis(Long repositoryId, String category, String summary,
                              String growthReason, String technicalInnovation,
                              String businessValue, String learningValue,
                              List<String> keyFiles) {
        this(repositoryId, null, null, null, category, summary, growthReason, technicalInnovation,
                businessValue, learningValue, keyFiles, null);
    }

    public RepositoryAnalysis(Long repositoryId, LocalDate analysisDate, String analysisType,
                              String modelRole, String category, String summary,
                              String growthReason, String technicalInnovation,
                              String businessValue, String learningValue,
                              List<String> keyFiles, String rawOutput) {
        this.repositoryId = repositoryId;
        this.analysisDate = analysisDate;
        this.analysisType = analysisType;
        this.modelRole = modelRole;
        this.category = category;
        this.summary = summary;
        this.growthReason = growthReason;
        this.technicalInnovation = technicalInnovation;
        this.businessValue = businessValue;
        this.learningValue = learningValue;
        this.keyFiles = keyFiles == null ? Collections.emptyList() : keyFiles;
        this.rawOutput = rawOutput;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getModelRole() {
        return modelRole;
    }

    public String getCategory() {
        return category;
    }

    public String getSummary() {
        return summary;
    }

    public String getGrowthReason() {
        return growthReason;
    }

    public String getTechnicalInnovation() {
        return technicalInnovation;
    }

    public String getBusinessValue() {
        return businessValue;
    }

    public String getLearningValue() {
        return learningValue;
    }

    public List<String> getKeyFiles() {
        return keyFiles;
    }

    public String getRawOutput() {
        return rawOutput;
    }
}
