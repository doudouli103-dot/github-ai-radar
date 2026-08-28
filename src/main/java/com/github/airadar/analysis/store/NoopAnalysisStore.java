package com.github.airadar.analysis.store;

import com.github.airadar.analysis.model.RepositoryAnalysis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(AnalysisStore.class)
public class NoopAnalysisStore implements AnalysisStore {

    @Override
    public void save(RepositoryAnalysis analysis) {
    }
}
