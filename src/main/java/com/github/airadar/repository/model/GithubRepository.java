package com.github.airadar.repository.model;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

public class GithubRepository {

    private final Long id;
    private final Long githubId;
    private final String owner;
    private final String name;
    private final String fullName;
    private final String htmlUrl;
    private final String description;
    private final String language;
    private final List<String> topics;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime pushedAt;
    private final String defaultBranch;
    private final int stars;
    private final int forks;
    private final int openIssues;

    public GithubRepository(Long id, String fullName, String description, String language,
                            List<String> topics, OffsetDateTime createdAt) {
        this(id, id, ownerFrom(fullName), nameFrom(fullName), fullName, null, description,
                language, topics, createdAt, null, null, 0, 0, 0);
    }

    public GithubRepository(Long id, Long githubId, String owner, String name, String fullName,
                            String htmlUrl, String description, String language, List<String> topics,
                            OffsetDateTime createdAt, OffsetDateTime pushedAt, String defaultBranch,
                            int stars, int forks, int openIssues) {
        this.id = id;
        this.githubId = githubId;
        this.owner = owner;
        this.name = name;
        this.fullName = fullName;
        this.htmlUrl = htmlUrl;
        this.description = description;
        this.language = language;
        this.topics = topics == null ? Collections.emptyList() : topics;
        this.createdAt = createdAt;
        this.pushedAt = pushedAt;
        this.defaultBranch = defaultBranch;
        this.stars = stars;
        this.forks = forks;
        this.openIssues = openIssues;
    }

    public Long getId() {
        return id;
    }

    public Long getGithubId() {
        return githubId;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getTopics() {
        return topics;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getPushedAt() {
        return pushedAt;
    }

    public String getDefaultBranch() {
        return defaultBranch;
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

    private static String ownerFrom(String fullName) {
        if (fullName == null || !fullName.contains("/")) {
            return null;
        }
        return fullName.substring(0, fullName.indexOf('/'));
    }

    private static String nameFrom(String fullName) {
        if (fullName == null || !fullName.contains("/")) {
            return fullName;
        }
        return fullName.substring(fullName.indexOf('/') + 1);
    }
}
