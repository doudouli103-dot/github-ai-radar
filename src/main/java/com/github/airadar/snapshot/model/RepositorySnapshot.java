package com.github.airadar.snapshot.model;

import java.time.LocalDate;

public class RepositorySnapshot {

    private final Long repositoryId;
    private final LocalDate snapshotDate;
    private final int stars;
    private final int forks;
    private final int openIssues;
    private final int commitCountRecent;
    private final int issueCountRecent;
    private final int prCountRecent;

    public RepositorySnapshot(Long repositoryId, LocalDate snapshotDate, int stars, int forks,
                              int openIssues, int commitCountRecent, int issueCountRecent,
                              int prCountRecent) {
        this.repositoryId = repositoryId;
        this.snapshotDate = snapshotDate;
        this.stars = stars;
        this.forks = forks;
        this.openIssues = openIssues;
        this.commitCountRecent = commitCountRecent;
        this.issueCountRecent = issueCountRecent;
        this.prCountRecent = prCountRecent;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public int getStars() {
        return stars;
    }

    public int getForks() {
        return forks;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public int getCommitCountRecent() {
        return commitCountRecent;
    }

    public int getIssueCountRecent() {
        return issueCountRecent;
    }

    public int getPrCountRecent() {
        return prCountRecent;
    }
}
