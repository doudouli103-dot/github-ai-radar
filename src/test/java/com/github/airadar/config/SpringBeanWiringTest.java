package com.github.airadar.config;

import com.github.airadar.analysis.gateway.AiGatewayClient;
import com.github.airadar.analysis.gateway.AiRequest;
import com.github.airadar.analysis.gateway.AiResponse;
import com.github.airadar.analysis.store.AnalysisStore;
import com.github.airadar.analysis.store.NoopAnalysisStore;
import com.github.airadar.github.service.GithubCollector;
import com.github.airadar.github.service.GithubRepositoryContextCollector;
import com.github.airadar.github.service.GithubRepositorySourceCollector;
import com.github.airadar.github.service.NoopGithubRepositoryContextCollector;
import com.github.airadar.github.service.NoopGithubRepositorySourceCollector;
import com.github.airadar.job.scheduler.DailyRadarScheduler;
import com.github.airadar.job.service.DailyRadarJobService;
import com.github.airadar.report.service.MarkdownReportService;
import com.github.airadar.report.store.DailyReportStore;
import com.github.airadar.report.store.NoopDailyReportStore;
import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.repository.store.NoopRepositoryStore;
import com.github.airadar.repository.store.RepositoryStore;
import com.github.airadar.scoring.service.HotScoreService;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpringBeanWiringTest {

    @Test
    void wiresDailyRadarServicesAsSpringBeans() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TestConfig.class);
        context.scan("com.github.airadar.scoring", "com.github.airadar.report.service",
                "com.github.airadar.analysis.prompt", "com.github.airadar.analysis.service",
                "com.github.airadar.job");
        context.refresh();

        assertThat(context.getBean(HotScoreService.class)).isNotNull();
        assertThat(context.getBean(MarkdownReportService.class)).isNotNull();
        assertThat(context.getBean(DailyRadarJobService.class)).isNotNull();
        assertThat(context.getBean(DailyRadarScheduler.class)).isNotNull();

        context.close();
    }

    @Configuration
    static class TestConfig {

        @Bean
        GithubCollector githubCollector() {
            return new GithubCollector() {
                @Override
                public List<GithubRepository> collectCandidates(LocalDate reportDate) {
                    return Collections.emptyList();
                }

                @Override
                public RepositorySnapshot collectTodaySnapshot(GithubRepository repository, LocalDate reportDate) {
                    return null;
                }

                @Override
                public RepositorySnapshot findYesterdaySnapshot(GithubRepository repository, LocalDate reportDate) {
                    return null;
                }
            };
        }

        @Bean
        AiGatewayClient aiGatewayClient() {
            return new AiGatewayClient() {
                @Override
                public AiResponse chat(AiRequest request) {
                    return new AiResponse("Unknown", "");
                }
            };
        }

        @Bean
        RepositoryStore repositoryStore() {
            return new NoopRepositoryStore();
        }

        @Bean
        GithubRepositoryContextCollector githubRepositoryContextCollector() {
            return new NoopGithubRepositoryContextCollector();
        }

        @Bean
        GithubRepositorySourceCollector githubRepositorySourceCollector() {
            return new NoopGithubRepositorySourceCollector();
        }

        @Bean
        AnalysisStore analysisStore() {
            return new NoopAnalysisStore();
        }

        @Bean
        DailyReportStore dailyReportStore() {
            return new NoopDailyReportStore();
        }
    }
}
