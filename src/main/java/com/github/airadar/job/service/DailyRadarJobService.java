package com.github.airadar.job.service;

import com.github.airadar.analysis.gateway.AiGatewayClient;
import com.github.airadar.analysis.gateway.AiRequest;
import com.github.airadar.analysis.gateway.AiResponse;
import com.github.airadar.analysis.model.RepositoryAnalysis;
import com.github.airadar.analysis.prompt.AnalysisPrompt;
import com.github.airadar.analysis.prompt.AnalysisPromptService;
import com.github.airadar.analysis.service.RepositoryAnalysisParser;
import com.github.airadar.analysis.store.AnalysisStore;
import com.github.airadar.analysis.store.NoopAnalysisStore;
import com.github.airadar.github.service.GithubCollector;
import com.github.airadar.github.service.GithubRepositoryContextCollector;
import com.github.airadar.github.service.GithubRepositorySourceCollector;
import com.github.airadar.github.service.NoopGithubRepositoryContextCollector;
import com.github.airadar.github.service.NoopGithubRepositorySourceCollector;
import com.github.airadar.report.model.DailyReport;
import com.github.airadar.report.service.MarkdownReportService;
import com.github.airadar.report.store.DailyReportStore;
import com.github.airadar.report.store.NoopDailyReportStore;
import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.repository.store.NoopRepositoryStore;
import com.github.airadar.repository.store.RepositoryStore;
import com.github.airadar.scoring.model.RepositoryScore;
import com.github.airadar.scoring.service.HotScoreService;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

@Service
public class DailyRadarJobService {

    private final GithubCollector githubCollector;
    private final GithubRepositoryContextCollector githubRepositoryContextCollector;
    private final GithubRepositorySourceCollector githubRepositorySourceCollector;
    private final AiGatewayClient aiGatewayClient;
    private final RepositoryStore repositoryStore;
    private final AnalysisStore analysisStore;
    private final DailyReportStore dailyReportStore;
    private final HotScoreService hotScoreService;
    private final MarkdownReportService markdownReportService;
    private final AnalysisPromptService analysisPromptService;
    private final RepositoryAnalysisParser repositoryAnalysisParser;

    public DailyRadarJobService(GithubCollector githubCollector, AiGatewayClient aiGatewayClient) {
        this(githubCollector, new NoopGithubRepositoryContextCollector(), new NoopGithubRepositorySourceCollector(),
                aiGatewayClient,
                new NoopRepositoryStore(), new NoopAnalysisStore(),
                new NoopDailyReportStore(), new HotScoreService(),
                new MarkdownReportService(), new AnalysisPromptService(), new RepositoryAnalysisParser());
    }

    public DailyRadarJobService(GithubCollector githubCollector,
                                GithubRepositoryContextCollector githubRepositoryContextCollector,
                                AiGatewayClient aiGatewayClient) {
        this(githubCollector, githubRepositoryContextCollector, new NoopGithubRepositorySourceCollector(),
                aiGatewayClient);
    }

    public DailyRadarJobService(GithubCollector githubCollector,
                                GithubRepositoryContextCollector githubRepositoryContextCollector,
                                GithubRepositorySourceCollector githubRepositorySourceCollector,
                                AiGatewayClient aiGatewayClient) {
        this(githubCollector, githubRepositoryContextCollector, githubRepositorySourceCollector,
                aiGatewayClient, new NoopRepositoryStore(),
                new NoopAnalysisStore(), new NoopDailyReportStore());
    }

    public DailyRadarJobService(GithubCollector githubCollector, AiGatewayClient aiGatewayClient,
                                RepositoryStore repositoryStore) {
        this(githubCollector, new NoopGithubRepositoryContextCollector(), new NoopGithubRepositorySourceCollector(),
                aiGatewayClient,
                repositoryStore, new NoopAnalysisStore(),
                new NoopDailyReportStore());
    }

    public DailyRadarJobService(GithubCollector githubCollector,
                                GithubRepositoryContextCollector githubRepositoryContextCollector,
                                AiGatewayClient aiGatewayClient,
                                RepositoryStore repositoryStore, AnalysisStore analysisStore,
                                DailyReportStore dailyReportStore) {
        this(githubCollector, githubRepositoryContextCollector, new NoopGithubRepositorySourceCollector(),
                aiGatewayClient, repositoryStore, analysisStore, dailyReportStore);
    }

    public DailyRadarJobService(GithubCollector githubCollector,
                                GithubRepositoryContextCollector githubRepositoryContextCollector,
                                GithubRepositorySourceCollector githubRepositorySourceCollector,
                                AiGatewayClient aiGatewayClient,
                                RepositoryStore repositoryStore, AnalysisStore analysisStore,
                                DailyReportStore dailyReportStore) {
        this(githubCollector, githubRepositoryContextCollector, githubRepositorySourceCollector, aiGatewayClient,
                repositoryStore, analysisStore, dailyReportStore,
                new HotScoreService(),
                new MarkdownReportService(), new AnalysisPromptService(), new RepositoryAnalysisParser());
    }

    @Autowired
    public DailyRadarJobService(GithubCollector githubCollector,
                                GithubRepositoryContextCollector githubRepositoryContextCollector,
                                GithubRepositorySourceCollector githubRepositorySourceCollector,
                                AiGatewayClient aiGatewayClient,
                                RepositoryStore repositoryStore,
                                AnalysisStore analysisStore,
                                DailyReportStore dailyReportStore,
                                HotScoreService hotScoreService,
                                MarkdownReportService markdownReportService,
                                AnalysisPromptService analysisPromptService,
                                RepositoryAnalysisParser repositoryAnalysisParser) {
        this.githubCollector = githubCollector;
        this.githubRepositoryContextCollector = githubRepositoryContextCollector;
        this.githubRepositorySourceCollector = githubRepositorySourceCollector;
        this.aiGatewayClient = aiGatewayClient;
        this.repositoryStore = repositoryStore;
        this.analysisStore = analysisStore;
        this.dailyReportStore = dailyReportStore;
        this.hotScoreService = hotScoreService;
        this.markdownReportService = markdownReportService;
        this.analysisPromptService = analysisPromptService;
        this.repositoryAnalysisParser = repositoryAnalysisParser;
    }

    public DailyReport run(LocalDate reportDate) {
        List<GithubRepository> candidates = githubCollector.collectCandidates(reportDate);
        List<RepositoryScore> scores = new ArrayList<RepositoryScore>();
        for (GithubRepository repository : candidates) {
            GithubRepository savedRepository = repositoryStore.upsertRepository(repository);
            RepositorySnapshot today = githubCollector.collectTodaySnapshot(savedRepository, reportDate);
            repositoryStore.saveSnapshot(today);
            RepositorySnapshot yesterday = repositoryStore.findSnapshot(savedRepository.getId(), reportDate.minusDays(1))
                    .orElse(githubCollector.findYesterdaySnapshot(savedRepository, reportDate));
            scores.add(hotScoreService.score(savedRepository, today, yesterday));
        }

        Collections.sort(scores, Comparator.comparingDouble(RepositoryScore::getHotScore).reversed());
        List<RepositoryScore> top10 = scores.subList(0, Math.min(10, scores.size()));
        List<RepositoryScore> top3 = top10.subList(0, Math.min(3, top10.size()));

        List<RepositoryAnalysis> analyses = new ArrayList<RepositoryAnalysis>();
        for (RepositoryScore score : top10) {
            AnalysisPrompt prompt = analysisPromptService.lightPrompt(score,
                    githubRepositoryContextCollector.collect(score.getRepository()));
            AiResponse response = aiGatewayClient.chat(new AiRequest("LIGHT_TOP10", "general",
                    prompt.getSystemPrompt(), prompt.getUserPrompt()));
            RepositoryAnalysis analysis = repositoryAnalysisParser.parse(score.getRepository().getId(),
                    reportDate, "LIGHT_TOP10", "general", response.getContent());
            analyses.add(analysis);
            analysisStore.save(analysis);
        }

        for (RepositoryScore score : top3) {
            AnalysisPrompt prompt = analysisPromptService.deepSourcePrompt(score,
                    githubRepositorySourceCollector.collect(score.getRepository()));
            AiResponse response = aiGatewayClient.chat(new AiRequest("DEEP_SOURCE_TOP3", "coder",
                    prompt.getSystemPrompt(), prompt.getUserPrompt()));
            RepositoryAnalysis analysis = repositoryAnalysisParser.parse(score.getRepository().getId(),
                    reportDate, "DEEP_SOURCE_TOP3", "coder", response.getContent());
            analyses.add(analysis);
            analysisStore.save(analysis);
        }

        String markdown = markdownReportService.render(reportDate, top10, analyses);
        String title = "GitHub AI Daily - " + reportDate;
        DailyReport report = new DailyReport(reportDate, title, markdown,
                topRepositoryIds(top10), "Fast-growing AI repositories ranked by HotScore.");
        dailyReportStore.save(report);
        return report;
    }

    private List<Long> topRepositoryIds(List<RepositoryScore> topScores) {
        List<Long> ids = new ArrayList<Long>();
        for (RepositoryScore score : topScores) {
            ids.add(score.getRepository().getId());
        }
        return ids;
    }
}
