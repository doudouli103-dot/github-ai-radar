package com.github.airadar.job.service;

import com.github.airadar.analysis.gateway.AiGatewayClient;
import com.github.airadar.analysis.gateway.AiRequest;
import com.github.airadar.analysis.gateway.AiResponse;
import com.github.airadar.analysis.model.RepositoryAnalysis;
import com.github.airadar.analysis.store.AnalysisStore;
import com.github.airadar.github.model.GithubRepositoryContext;
import com.github.airadar.github.model.GithubRepositorySourceContext;
import com.github.airadar.github.model.GithubSourceFile;
import com.github.airadar.github.service.GithubCollector;
import com.github.airadar.github.service.GithubRepositoryContextCollector;
import com.github.airadar.github.service.GithubRepositorySourceCollector;
import com.github.airadar.report.model.DailyReport;
import com.github.airadar.report.store.DailyReportStore;
import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.repository.store.RepositoryStore;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DailyRadarJobServiceTest {

    @Test
    void runsDailyWorkflowAndGeneratesReport() {
        RecordingAiGatewayClient aiGatewayClient = new RecordingAiGatewayClient();
        DailyRadarJobService service = new DailyRadarJobService(new FakeGithubCollector(),
                new FakeGithubRepositoryContextCollector(), new FakeGithubRepositorySourceCollector(), aiGatewayClient);

        DailyReport report = service.run(LocalDate.of(2026, 8, 27));

        assertThat(report.getTitle()).isEqualTo("GitHub AI Daily - 2026-08-27");
        assertThat(report.getMarkdownContent()).contains("owner/agent-9");
        assertThat(aiGatewayClient.requests).hasSize(13);
        assertThat(aiGatewayClient.requests.get(0).getModelRole()).isEqualTo("general");
        assertThat(aiGatewayClient.requests.get(0).getUserPrompt()).contains("README for owner/agent-1");
        assertThat(aiGatewayClient.requests.get(0).getUserPrompt()).contains("Commit for owner/agent-1");
        assertThat(aiGatewayClient.requests.get(0).getUserPrompt()).contains("Issue for owner/agent-1");
        assertThat(aiGatewayClient.requests.get(9).getModelRole()).isEqualTo("general");
        assertThat(aiGatewayClient.requests.get(10).getModelRole()).isEqualTo("coder");
        assertThat(aiGatewayClient.requests.get(10).getUserPrompt()).contains("src/agent/loop.py");
        assertThat(aiGatewayClient.requests.get(10).getUserPrompt()).contains("class AgentLoop");
        assertThat(aiGatewayClient.requests.get(12).getModelRole()).isEqualTo("coder");
    }

    @Test
    void persistsRepositoriesAndSnapshotsBeforeScoring() {
        RecordingRepositoryStore repositoryStore = new RecordingRepositoryStore();
        RecordingAnalysisStore analysisStore = new RecordingAnalysisStore();
        RecordingDailyReportStore reportStore = new RecordingDailyReportStore();
        DailyRadarJobService service = new DailyRadarJobService(new FakeGithubCollector(),
                new FakeGithubRepositoryContextCollector(), new FakeGithubRepositorySourceCollector(),
                new RecordingAiGatewayClient(),
                repositoryStore, analysisStore, reportStore);

        DailyReport report = service.run(LocalDate.of(2026, 8, 27));

        assertThat(repositoryStore.repositories).hasSize(12);
        assertThat(repositoryStore.snapshots).hasSize(12);
        assertThat(repositoryStore.requestedSnapshotDates).contains(LocalDate.of(2026, 8, 26));
        assertThat(repositoryStore.snapshots.get(0).getRepositoryId()).isEqualTo(101L);
        assertThat(report.getMarkdownContent()).contains("### Repository 101");
        assertThat(analysisStore.analyses).hasSize(13);
        assertThat(analysisStore.analyses.get(0).getAnalysisDate()).isEqualTo(LocalDate.of(2026, 8, 27));
        assertThat(analysisStore.analyses.get(0).getAnalysisType()).isEqualTo("LIGHT_TOP10");
        assertThat(analysisStore.analyses.get(10).getAnalysisType()).isEqualTo("DEEP_SOURCE_TOP3");
        assertThat(analysisStore.analyses.get(0).getSummary()).isEqualTo("structured general summary");
        assertThat(analysisStore.analyses.get(10).getKeyFiles()).containsExactly("src/agent/loop.py");
        assertThat(reportStore.reports).containsExactly(report);
        assertThat(report.getTopRepositoryIds()).hasSize(10);
    }

    private static class FakeGithubCollector implements GithubCollector {

        @Override
        public List<GithubRepository> collectCandidates(LocalDate reportDate) {
            List<GithubRepository> repositories = new ArrayList<GithubRepository>();
            for (long i = 1; i <= 12; i++) {
                repositories.add(new GithubRepository(i, "owner/agent-" + i,
                        "AI agent project " + i, "Python",
                        Arrays.asList("agent", "llm"), OffsetDateTime.now().minusDays(10)));
            }
            return repositories;
        }

        @Override
        public RepositorySnapshot collectTodaySnapshot(GithubRepository repository, LocalDate reportDate) {
            int base = repository.getId().intValue() * 100;
            return new RepositorySnapshot(repository.getId(), reportDate, base + 1000,
                    base / 2, 20, repository.getId().intValue(), 5, 2);
        }

        @Override
        public RepositorySnapshot findYesterdaySnapshot(GithubRepository repository, LocalDate reportDate) {
            int base = repository.getId().intValue() * 100;
            return new RepositorySnapshot(repository.getId(), reportDate.minusDays(1),
                    base, base / 2 - 10, 10, 1, 1, 1);
        }
    }

    private static class RecordingAiGatewayClient implements AiGatewayClient {

        private final List<AiRequest> requests = new ArrayList<AiRequest>();

        @Override
        public AiResponse chat(AiRequest request) {
            requests.add(request);
            if ("coder".equals(request.getModelRole())) {
                return new AiResponse("Unknown", "{"
                        + "\"category\":\"Coding\","
                        + "\"architectureSummary\":\"structured coder summary\","
                        + "\"growthReason\":\"source architecture attention\","
                        + "\"technicalInnovation\":\"agent loop\","
                        + "\"businessValue\":\"developer workflow\","
                        + "\"learningSuggestions\":[\"read loop\"],"
                        + "\"keyFiles\":[{\"path\":\"src/agent/loop.py\",\"reason\":\"agent loop\"}]"
                        + "}");
            }
            return new AiResponse("Unknown", "{"
                    + "\"category\":\"Agent\","
                    + "\"summary\":\"structured general summary\","
                    + "\"growthReason\":\"community growth\","
                    + "\"technicalInnovation\":\"tool calling\","
                    + "\"businessValue\":\"developer productivity\","
                    + "\"learningValue\":\"study agent design\","
                    + "\"worthFurtherStudy\":true"
                    + "}");
        }
    }

    private static class FakeGithubRepositoryContextCollector implements GithubRepositoryContextCollector {

        @Override
        public GithubRepositoryContext collect(GithubRepository repository) {
            return new GithubRepositoryContext("README for " + repository.getFullName(),
                    Arrays.asList("Commit for " + repository.getFullName()),
                    Arrays.asList("Issue for " + repository.getFullName()));
        }
    }

    private static class FakeGithubRepositorySourceCollector implements GithubRepositorySourceCollector {

        @Override
        public GithubRepositorySourceContext collect(GithubRepository repository) {
            return new GithubRepositorySourceContext(Arrays.asList(
                    new GithubSourceFile("src/agent/loop.py", "class AgentLoop:\n    pass\n"),
                    new GithubSourceFile("src/agent/tools.py", "def call_tool():\n    pass\n")
            ));
        }
    }

    private static class RecordingRepositoryStore implements RepositoryStore {

        private final List<GithubRepository> repositories = new ArrayList<GithubRepository>();
        private final List<RepositorySnapshot> snapshots = new ArrayList<RepositorySnapshot>();
        private final List<LocalDate> requestedSnapshotDates = new ArrayList<LocalDate>();

        @Override
        public GithubRepository upsertRepository(GithubRepository repository) {
            repositories.add(repository);
            return new GithubRepository(repository.getId() + 100, repository.getGithubId(),
                    repository.getOwner(), repository.getName(), repository.getFullName(),
                    repository.getHtmlUrl(), repository.getDescription(), repository.getLanguage(),
                    repository.getTopics(), repository.getCreatedAt(), repository.getPushedAt(),
                    repository.getDefaultBranch(), repository.getStars(), repository.getForks(),
                    repository.getOpenIssues());
        }

        @Override
        public void saveSnapshot(RepositorySnapshot snapshot) {
            snapshots.add(snapshot);
        }

        @Override
        public Optional<RepositorySnapshot> findSnapshot(Long repositoryId, LocalDate snapshotDate) {
            requestedSnapshotDates.add(snapshotDate);
            return Optional.empty();
        }
    }

    private static class RecordingAnalysisStore implements AnalysisStore {

        private final List<RepositoryAnalysis> analyses = new ArrayList<RepositoryAnalysis>();

        @Override
        public void save(RepositoryAnalysis analysis) {
            analyses.add(analysis);
        }
    }

    private static class RecordingDailyReportStore implements DailyReportStore {

        private final List<DailyReport> reports = new ArrayList<DailyReport>();

        @Override
        public void save(DailyReport report) {
            reports.add(report);
        }
    }
}
