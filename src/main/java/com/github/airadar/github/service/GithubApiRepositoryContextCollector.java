package com.github.airadar.github.service;

import com.github.airadar.github.dto.GithubCommitItem;
import com.github.airadar.github.dto.GithubIssueItem;
import com.github.airadar.github.dto.GithubReadmeResponse;
import com.github.airadar.github.model.GithubRepositoryContext;
import com.github.airadar.repository.model.GithubRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class GithubApiRepositoryContextCollector implements GithubRepositoryContextCollector {

    private final WebClient webClient;

    public GithubApiRepositoryContextCollector(WebClient.Builder webClientBuilder,
                                               @Value("${github.token:}") String githubToken) {
        WebClient.Builder builder = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28");
        if (githubToken != null && !githubToken.trim().isEmpty()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken.trim());
        }
        this.webClient = builder.build();
    }

    GithubApiRepositoryContextCollector(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public GithubRepositoryContext collect(GithubRepository repository) {
        return new GithubRepositoryContext(
                readme(repository),
                recentCommits(repository),
                recentIssues(repository)
        );
    }

    private String readme(GithubRepository repository) {
        GithubReadmeResponse response = webClient.get()
                .uri("/repos/{owner}/{repo}/readme", repository.getOwner(), repository.getName())
                .retrieve()
                .bodyToMono(GithubReadmeResponse.class)
                .onErrorReturn(new GithubReadmeResponse())
                .block();
        if (response == null || response.getContent() == null) {
            return "";
        }
        if ("base64".equalsIgnoreCase(response.getEncoding())) {
            byte[] decoded = Base64.getMimeDecoder().decode(response.getContent());
            return new String(decoded, StandardCharsets.UTF_8);
        }
        return response.getContent();
    }

    private List<String> recentCommits(GithubRepository repository) {
        List<GithubCommitItem> items = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/commits")
                        .queryParam("per_page", 5)
                        .build(repository.getOwner(), repository.getName()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GithubCommitItem>>() {
                })
                .onErrorReturn(new ArrayList<GithubCommitItem>())
                .block();
        List<String> messages = new ArrayList<String>();
        if (items == null) {
            return messages;
        }
        for (GithubCommitItem item : items) {
            if (item.getCommit() != null && item.getCommit().getMessage() != null) {
                messages.add(firstLine(item.getCommit().getMessage()));
            }
        }
        return messages;
    }

    private List<String> recentIssues(GithubRepository repository) {
        List<GithubIssueItem> items = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/issues")
                        .queryParam("state", "open")
                        .queryParam("per_page", 5)
                        .build(repository.getOwner(), repository.getName()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GithubIssueItem>>() {
                })
                .onErrorReturn(new ArrayList<GithubIssueItem>())
                .block();
        List<String> titles = new ArrayList<String>();
        if (items == null) {
            return titles;
        }
        for (GithubIssueItem item : items) {
            if (item.getTitle() != null) {
                titles.add(item.getTitle());
            }
        }
        return titles;
    }

    private String firstLine(String value) {
        int newline = value.indexOf('\n');
        if (newline < 0) {
            return value;
        }
        return value.substring(0, newline);
    }
}
