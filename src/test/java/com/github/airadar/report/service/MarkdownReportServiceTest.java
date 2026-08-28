package com.github.airadar.report.service;

import com.github.airadar.analysis.model.RepositoryAnalysis;
import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.scoring.model.RepositoryScore;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import com.github.airadar.scoring.service.HotScoreService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownReportServiceTest {

    @Test
    void rendersDailyReportWithTrendTopTableAndDeepAnalysis() {
        GithubRepository repository = new GithubRepository(1L, "owner/fast-agent",
                "AI agent framework", "Python", Arrays.asList("agent", "llm"),
                OffsetDateTime.now().minusDays(20));
        RepositoryScore score = new HotScoreService().score(repository,
                new RepositorySnapshot(1L, LocalDate.of(2026, 8, 27), 4200, 500, 80, 40, 30, 10),
                new RepositorySnapshot(1L, LocalDate.of(2026, 8, 26), 3000, 350, 65, 10, 6, 2));
        RepositoryAnalysis analysis = new RepositoryAnalysis(1L, "Agent",
                "Builds agent applications", "Rapid adoption by coding agent users",
                "Composable tool calling loop", "Useful for developer tooling",
                "Read the planner and tool modules", Arrays.asList("src/agent/loop.py"));

        String markdown = new MarkdownReportService().render(LocalDate.of(2026, 8, 27),
                Collections.singletonList(score), Collections.singletonList(analysis));

        assertThat(markdown).contains("# GitHub AI Daily - 2026-08-27");
        assertThat(markdown).contains("owner/fast-agent");
        assertThat(markdown).contains("Star Growth");
        assertThat(markdown).contains("Rapid adoption by coding agent users");
        assertThat(markdown).contains("src/agent/loop.py");
        assertThat(markdown).contains("Today's Most Worth Studying");
    }
}
