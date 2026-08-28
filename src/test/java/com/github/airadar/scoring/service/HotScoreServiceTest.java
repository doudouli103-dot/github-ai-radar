package com.github.airadar.scoring.service;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.scoring.model.RepositoryScore;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class HotScoreServiceTest {

    private final HotScoreService service = new HotScoreService();

    @Test
    void scoresFastGrowingAiRepositoryHigherThanMatureSlowRepository() {
        RepositoryScore fast = service.score(
                new GithubRepository(1L, "owner/fast-agent", "AI agent framework",
                        "Python", Arrays.asList("agent", "llm"), OffsetDateTime.now().minusDays(20)),
                new RepositorySnapshot(1L, LocalDate.of(2026, 8, 27), 4200, 500, 80, 40, 30, 10),
                new RepositorySnapshot(1L, LocalDate.of(2026, 8, 26), 3000, 350, 65, 10, 6, 2)
        );

        RepositoryScore mature = service.score(
                new GithubRepository(2L, "owner/mature", "General machine learning toolkit",
                        "Python", Arrays.asList("machine-learning"), OffsetDateTime.now().minusYears(5)),
                new RepositorySnapshot(2L, LocalDate.of(2026, 8, 27), 200000, 20000, 1000, 5, 4, 1),
                new RepositorySnapshot(2L, LocalDate.of(2026, 8, 26), 199980, 19995, 990, 4, 3, 1)
        );

        assertThat(fast.getHotScore()).isGreaterThan(mature.getHotScore());
        assertThat(fast.getStarGrowth()).isEqualTo(1200);
        assertThat(fast.getAiRelevance()).isGreaterThanOrEqualTo(70);
    }

    @Test
    void treatsMissingYesterdaySnapshotAsZeroGrowthBaseline() {
        RepositoryScore score = service.score(
                new GithubRepository(1L, "owner/rag", "RAG system with tool calling",
                        "TypeScript", Arrays.asList("rag", "ai"), OffsetDateTime.now().minusDays(5)),
                new RepositorySnapshot(1L, LocalDate.of(2026, 8, 27), 100, 20, 5, 8, 3, 1),
                null
        );

        assertThat(score.getStarGrowth()).isEqualTo(0);
        assertThat(score.getForkGrowth()).isEqualTo(0);
        assertThat(score.getHotScore()).isGreaterThan(0);
    }
}
