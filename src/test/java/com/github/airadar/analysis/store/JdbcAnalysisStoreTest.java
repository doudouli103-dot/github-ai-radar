package com.github.airadar.analysis.store;

import com.github.airadar.analysis.model.RepositoryAnalysis;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JdbcAnalysisStoreTest {

    @Test
    void savesRepositoryAnalysis() {
        NamedParameterJdbcOperations jdbc = mock(NamedParameterJdbcOperations.class);
        JdbcAnalysisStore store = new JdbcAnalysisStore(jdbc);

        store.save(new RepositoryAnalysis(77L, LocalDate.of(2026, 8, 27),
                "LIGHT_TOP10", "general", "Agent", "Builds agents",
                "Star growth", "Tool loop", "Dev tooling",
                "Read architecture", Arrays.asList("src/agent/loop.py"),
                "{\"category\":\"Agent\"}"));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbc).update(sqlCaptor.capture(), paramsCaptor.capture());

        assertThat(sqlCaptor.getValue()).contains("INSERT INTO repository_analysis");
        assertThat(paramsCaptor.getValue().getValue("repositoryId")).isEqualTo(77L);
        assertThat(paramsCaptor.getValue().getValue("analysisDate")).isEqualTo(LocalDate.of(2026, 8, 27));
        assertThat(paramsCaptor.getValue().getValue("analysisType")).isEqualTo("LIGHT_TOP10");
        assertThat(paramsCaptor.getValue().getValue("modelRole")).isEqualTo("general");
        assertThat(paramsCaptor.getValue().getValue("keyFiles").toString()).contains("src/agent/loop.py");
    }
}
