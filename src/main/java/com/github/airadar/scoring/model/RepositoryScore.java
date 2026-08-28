package com.github.airadar.scoring.model;

import com.github.airadar.repository.model.GithubRepository;

public class RepositoryScore {

    private final GithubRepository repository;
    private final int starGrowth;
    private final int forkGrowth;
    private final int commitActivity;
    private final int issueActivity;
    private final int aiRelevance;
    private final int freshness;
    private final double hotScore;

    public RepositoryScore(GithubRepository repository, int starGrowth, int forkGrowth,
                           int commitActivity, int issueActivity, int aiRelevance,
                           int freshness, double hotScore) {
        this.repository = repository;
        this.starGrowth = starGrowth;
        this.forkGrowth = forkGrowth;
        this.commitActivity = commitActivity;
        this.issueActivity = issueActivity;
        this.aiRelevance = aiRelevance;
        this.freshness = freshness;
        this.hotScore = hotScore;
    }

    public GithubRepository getRepository() {
        return repository;
    }

    public int getStarGrowth() {
        return starGrowth;
    }

    public int getForkGrowth() {
        return forkGrowth;
    }

    public int getCommitActivity() {
        return commitActivity;
    }

    public int getIssueActivity() {
        return issueActivity;
    }

    public int getAiRelevance() {
        return aiRelevance;
    }

    public int getFreshness() {
        return freshness;
    }

    public double getHotScore() {
        return hotScore;
    }
}
