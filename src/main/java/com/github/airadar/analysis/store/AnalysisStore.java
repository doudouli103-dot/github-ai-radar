package com.github.airadar.analysis.store;

import com.github.airadar.analysis.model.RepositoryAnalysis;

public interface AnalysisStore {

    void save(RepositoryAnalysis analysis);
}
