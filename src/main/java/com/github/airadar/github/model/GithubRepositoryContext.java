package com.github.airadar.github.model;

import java.util.Collections;
import java.util.List;

public class GithubRepositoryContext {

    private final String readme;
    private final List<String> recentCommits;
    private final List<String> recentIssues;

    public GithubRepositoryContext(String readme, List<String> recentCommits, List<String> recentIssues) {
        this.readme = readme;
        this.recentCommits = recentCommits == null ? Collections.<String>emptyList() : recentCommits;
        this.recentIssues = recentIssues == null ? Collections.<String>emptyList() : recentIssues;
    }

    public static GithubRepositoryContext empty() {
        return new GithubRepositoryContext("", Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    public String getReadme() {
        return readme;
    }

    public List<String> getRecentCommits() {
        return recentCommits;
    }

    public List<String> getRecentIssues() {
        return recentIssues;
    }
}
