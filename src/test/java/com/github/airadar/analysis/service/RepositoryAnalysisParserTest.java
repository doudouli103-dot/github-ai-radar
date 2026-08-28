package com.github.airadar.analysis.service;

import com.github.airadar.analysis.model.RepositoryAnalysis;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryAnalysisParserTest {

    @Test
    void parsesLightAnalysisJson() {
        String json = "{"
                + "\"category\":\"Agent\","
                + "\"summary\":\"Builds agent applications\","
                + "\"growthReason\":\"Popular with coding agent users\","
                + "\"technicalInnovation\":\"Composable tool loop\","
                + "\"businessValue\":\"Developer productivity\","
                + "\"learningValue\":\"Study planning and tools\","
                + "\"worthFurtherStudy\":true"
                + "}";

        RepositoryAnalysis analysis = new RepositoryAnalysisParser()
                .parse(77L, LocalDate.of(2026, 8, 27),
                        "LIGHT_TOP10", "general", json);

        assertThat(analysis.getRepositoryId()).isEqualTo(77L);
        assertThat(analysis.getCategory()).isEqualTo("Agent");
        assertThat(analysis.getSummary()).isEqualTo("Builds agent applications");
        assertThat(analysis.getGrowthReason()).isEqualTo("Popular with coding agent users");
        assertThat(analysis.getTechnicalInnovation()).isEqualTo("Composable tool loop");
        assertThat(analysis.getBusinessValue()).isEqualTo("Developer productivity");
        assertThat(analysis.getLearningValue()).isEqualTo("Study planning and tools");
        assertThat(analysis.getRawOutput()).isEqualTo(json);
    }

    @Test
    void parsesDeepSourceJsonKeyFiles() {
        String json = "{"
                + "\"category\":\"Coding\","
                + "\"architectureSummary\":\"Planner calls tools in a loop\","
                + "\"growthReason\":\"New coding workflow\","
                + "\"technicalInnovation\":\"Context-aware tool routing\","
                + "\"businessValue\":\"Can become IDE infra\","
                + "\"learningSuggestions\":[\"Read loop\"],"
                + "\"keyFiles\":[{\"path\":\"src/agent/loop.py\",\"reason\":\"agent loop\"}]"
                + "}";

        RepositoryAnalysis analysis = new RepositoryAnalysisParser()
                .parse(77L, LocalDate.of(2026, 8, 27),
                        "DEEP_SOURCE_TOP3", "coder", json);

        assertThat(analysis.getCategory()).isEqualTo("Coding");
        assertThat(analysis.getSummary()).isEqualTo("Planner calls tools in a loop");
        assertThat(analysis.getLearningValue()).contains("Read loop");
        assertThat(analysis.getKeyFiles()).containsExactly("src/agent/loop.py");
    }

    @Test
    void fallsBackToRawTextWhenModelDoesNotReturnJson() {
        RepositoryAnalysis analysis = new RepositoryAnalysisParser()
                .parse(77L, LocalDate.of(2026, 8, 27),
                        "LIGHT_TOP10", "general", "plain text analysis");

        assertThat(analysis.getCategory()).isEqualTo("Unknown");
        assertThat(analysis.getSummary()).isEqualTo("plain text analysis");
        assertThat(analysis.getRawOutput()).isEqualTo("{\"content\":\"plain text analysis\"}");
    }
}
