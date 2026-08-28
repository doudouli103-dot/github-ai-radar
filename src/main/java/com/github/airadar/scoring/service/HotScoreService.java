package com.github.airadar.scoring.service;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.scoring.model.RepositoryScore;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class HotScoreService {

    private static final Set<String> AI_KEYWORDS = new HashSet<String>();

    static {
        AI_KEYWORDS.add("ai");
        AI_KEYWORDS.add("llm");
        AI_KEYWORDS.add("agent");
        AI_KEYWORDS.add("rag");
        AI_KEYWORDS.add("mcp");
        AI_KEYWORDS.add("model");
        AI_KEYWORDS.add("prompt");
        AI_KEYWORDS.add("tool calling");
        AI_KEYWORDS.add("generative");
        AI_KEYWORDS.add("copilot");
    }

    public RepositoryScore score(GithubRepository repository, RepositorySnapshot today,
                                 RepositorySnapshot yesterday) {
        int starGrowth = yesterday == null ? 0 : Math.max(0, today.getStars() - yesterday.getStars());
        int forkGrowth = yesterday == null ? 0 : Math.max(0, today.getForks() - yesterday.getForks());
        int commitActivity = today.getCommitCountRecent();
        int issueActivity = today.getIssueCountRecent() + today.getPrCountRecent();
        int aiRelevance = calculateAiRelevance(repository);
        int freshness = calculateFreshness(repository);

        double hotScore = normalized(starGrowth, 1200) * 35
                + normalized(forkGrowth, 200) * 15
                + normalized(commitActivity, 50) * 15
                + normalized(issueActivity, 40) * 10
                + normalized(aiRelevance, 100) * 15
                + normalized(freshness, 100) * 10;

        return new RepositoryScore(repository, starGrowth, forkGrowth, commitActivity,
                issueActivity, aiRelevance, freshness, hotScore);
    }

    private int calculateAiRelevance(GithubRepository repository) {
        int score = 0;
        String description = lower(repository.getDescription());
        String fullName = lower(repository.getFullName());

        for (String topic : repository.getTopics()) {
            String normalizedTopic = lower(topic);
            if (AI_KEYWORDS.contains(normalizedTopic)) {
                score += 20;
            }
        }

        for (String keyword : AI_KEYWORDS) {
            if (description.contains(keyword)) {
                score += 15;
            }
            if (fullName.contains(keyword)) {
                score += 10;
            }
        }

        String language = lower(repository.getLanguage());
        if ("python".equals(language) || "typescript".equals(language) || "java".equals(language)) {
            score += 5;
        }

        return Math.min(score, 100);
    }

    private int calculateFreshness(GithubRepository repository) {
        OffsetDateTime createdAt = repository.getCreatedAt();
        if (createdAt == null) {
            return 30;
        }
        long days = ChronoUnit.DAYS.between(createdAt, OffsetDateTime.now());
        if (days <= 30) {
            return 100;
        }
        if (days <= 90) {
            return 75;
        }
        if (days <= 365) {
            return 45;
        }
        return 15;
    }

    private double normalized(int value, int max) {
        if (value <= 0) {
            return 0;
        }
        return Math.min(1.0, value / (double) max);
    }

    private String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
