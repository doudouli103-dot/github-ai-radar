package com.github.airadar.analysis.prompt;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.github.model.GithubRepositoryContext;
import com.github.airadar.scoring.model.RepositoryScore;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class AnalysisPromptServiceTest {

    @Test
    void buildsLightAnalysisPromptWithStructuredJsonRequirement() {
        GithubRepository repository = new GithubRepository(1L, "owner/agent",
                "AI agent framework", "Python", Arrays.asList("agent", "llm"),
                OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        RepositoryScore score = new RepositoryScore(repository, 1200, 150,
                40, 20, 90, 100, 92.5);

        AnalysisPrompt prompt = new AnalysisPromptService().lightPrompt(score);

        assertThat(prompt.getSystemPrompt()).contains("valid JSON");
        assertThat(prompt.getUserPrompt()).contains("owner/agent");
        assertThat(prompt.getUserPrompt()).contains("Star growth: 1200");
        assertThat(prompt.getUserPrompt()).contains("businessValue");
        assertThat(prompt.getUserPrompt()).contains("worthFurtherStudy");
    }

    @Test
    void includesReadmeCommitsAndIssuesInLightPrompt() {
        GithubRepository repository = new GithubRepository(1L, "owner/agent",
                "AI agent framework", "Python", Arrays.asList("agent", "llm"),
                OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        RepositoryScore score = new RepositoryScore(repository, 1200, 150,
                40, 20, 90, 100, 92.5);
        GithubRepositoryContext context = new GithubRepositoryContext("README says tool calling",
                Arrays.asList("Add planner"), Arrays.asList("Need MCP support"));

        AnalysisPrompt prompt = new AnalysisPromptService().lightPrompt(score, context);

        assertThat(prompt.getUserPrompt()).contains("README says tool calling");
        assertThat(prompt.getUserPrompt()).contains("Add planner");
        assertThat(prompt.getUserPrompt()).contains("Need MCP support");
    }

    @Test
    void buildsDeepSourcePromptWithCodeArchitectureFocus() {
        GithubRepository repository = new GithubRepository(1L, "owner/agent",
                "AI agent framework", "Python", Arrays.asList("agent", "llm"),
                OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        RepositoryScore score = new RepositoryScore(repository, 1200, 150,
                40, 20, 90, 100, 92.5);

        AnalysisPrompt prompt = new AnalysisPromptService().deepSourcePrompt(score);

        assertThat(prompt.getSystemPrompt()).contains("source architecture");
        assertThat(prompt.getUserPrompt()).contains("Agent Loop");
        assertThat(prompt.getUserPrompt()).contains("Tool Calling");
        assertThat(prompt.getUserPrompt()).contains("keyFiles");
    }
}
