package com.github.airadar.github.service;

import com.github.airadar.github.dto.GithubSearchRepositoryItem;
import com.github.airadar.github.dto.GithubSearchResponse;
import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GithubApiRepositoryCollector implements GithubCollector {

    private final WebClient webClient;
    private final GithubSearchQueryFactory queryFactory;

    public GithubApiRepositoryCollector(WebClient.Builder webClientBuilder,
                                        GithubSearchQueryFactory queryFactory,
                                        @Value("${github.token:}") String githubToken) {
        WebClient.Builder builder = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28");
        if (githubToken != null && !githubToken.trim().isEmpty()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken.trim());
        }
        this.webClient = builder.build();
        this.queryFactory = queryFactory;
    }

    GithubApiRepositoryCollector(WebClient webClient, GithubSearchQueryFactory queryFactory) {
        this.webClient = webClient;
        this.queryFactory = queryFactory;
    }

    @Override
    public List<GithubRepository> collectCandidates(LocalDate reportDate) {
        Map<Long, GithubRepository> repositories = new LinkedHashMap<Long, GithubRepository>();
        for (String query : queryFactory.buildQueries(reportDate)) {
            GithubSearchResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search/repositories")
                            .queryParam("q", query)
                            .queryParam("sort", "stars")
                            .queryParam("order", "desc")
                            .queryParam("per_page", 30)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(GithubSearchResponse.class)
                    .block();

            if (response == null || response.getItems() == null) {
                continue;
            }
            for (GithubSearchRepositoryItem item : response.getItems()) {
                repositories.putIfAbsent(item.getId(), toRepository(item));
            }
        }
        return new ArrayList<GithubRepository>(repositories.values());
    }

    @Override
    public RepositorySnapshot collectTodaySnapshot(GithubRepository repository, LocalDate reportDate) {
        return new RepositorySnapshot(repository.getId(), reportDate, repository.getStars(),
                repository.getForks(), repository.getOpenIssues(), 0, 0, 0);
    }

    @Override
    public RepositorySnapshot findYesterdaySnapshot(GithubRepository repository, LocalDate reportDate) {
        return null;
    }

    private GithubRepository toRepository(GithubSearchRepositoryItem item) {
        String fullName = item.getFullName();
        String owner = ownerFrom(fullName);
        String name = item.getName() == null ? nameFrom(fullName) : item.getName();
        return new GithubRepository(item.getId(), item.getId(), owner, name, fullName,
                item.getHtmlUrl(), item.getDescription(), item.getLanguage(), item.getTopics(),
                item.getCreatedAt(), item.getPushedAt(), item.getDefaultBranch(),
                item.getStargazersCount(), item.getForksCount(), item.getOpenIssuesCount());
    }

    private String ownerFrom(String fullName) {
        if (fullName == null || !fullName.contains("/")) {
            return null;
        }
        return fullName.substring(0, fullName.indexOf('/'));
    }

    private String nameFrom(String fullName) {
        if (fullName == null || !fullName.contains("/")) {
            return fullName;
        }
        return fullName.substring(fullName.indexOf('/') + 1);
    }
}
