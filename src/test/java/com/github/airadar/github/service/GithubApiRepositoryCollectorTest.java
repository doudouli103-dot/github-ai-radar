package com.github.airadar.github.service;

import com.github.airadar.github.config.GithubSearchProperties;
import com.github.airadar.github.dto.GithubSearchRepositoryItem;
import com.github.airadar.github.dto.GithubSearchResponse;
import com.github.airadar.repository.model.GithubRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GithubApiRepositoryCollectorTest {

    @Test
    void collectsSearchResultsAndDeduplicatesByGithubId() {
        GithubSearchProperties properties = new GithubSearchProperties();
        properties.setMinStars(50);
        properties.setPushedAfterDays(30);
        properties.setTopics(Arrays.asList("agent", "llm"));

        GithubSearchRepositoryItem first = new GithubSearchRepositoryItem();
        first.setId(100L);
        first.setFullName("owner/agent");
        first.setHtmlUrl("https://github.com/owner/agent");
        first.setDescription("AI agent framework");
        first.setLanguage("Python");
        first.setStargazersCount(4200);
        first.setForksCount(500);
        first.setOpenIssuesCount(80);
        first.setTopics(Arrays.asList("agent", "llm"));
        first.setCreatedAt(OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        first.setPushedAt(OffsetDateTime.parse("2026-08-27T00:00:00Z"));
        first.setDefaultBranch("main");

        GithubSearchResponse response = new GithubSearchResponse();
        response.setItems(Arrays.asList(first, first));

        GithubApiRepositoryCollector collector = new GithubApiRepositoryCollector(
                WebClient.builder().exchangeFunction(fakeExchange(response)).build(),
                new GithubSearchQueryFactory(properties)
        );

        List<GithubRepository> repositories = collector.collectCandidates(LocalDate.of(2026, 8, 27));

        assertThat(repositories).hasSize(1);
        GithubRepository repository = repositories.get(0);
        assertThat(repository.getGithubId()).isEqualTo(100L);
        assertThat(repository.getOwner()).isEqualTo("owner");
        assertThat(repository.getName()).isEqualTo("agent");
        assertThat(repository.getFullName()).isEqualTo("owner/agent");
        assertThat(repository.getStars()).isEqualTo(4200);
        assertThat(repository.getForks()).isEqualTo(500);
        assertThat(repository.getOpenIssues()).isEqualTo(80);
    }

    private ExchangeFunction fakeExchange(GithubSearchResponse response) {
        return request -> reactor.core.publisher.Mono.just(
                org.springframework.web.reactive.function.client.ClientResponse
                        .create(org.springframework.http.HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body("{\"items\":[{\"id\":" + response.getItems().get(0).getId()
                                + ",\"full_name\":\"owner/agent\",\"html_url\":\"https://github.com/owner/agent\","
                                + "\"description\":\"AI agent framework\",\"language\":\"Python\","
                                + "\"stargazers_count\":4200,\"forks_count\":500,\"open_issues_count\":80,"
                                + "\"topics\":[\"agent\",\"llm\"],"
                                + "\"created_at\":\"2026-08-01T00:00:00Z\","
                                + "\"pushed_at\":\"2026-08-27T00:00:00Z\",\"default_branch\":\"main\"},"
                                + "{\"id\":" + response.getItems().get(0).getId()
                                + ",\"full_name\":\"owner/agent\",\"html_url\":\"https://github.com/owner/agent\","
                                + "\"description\":\"AI agent framework\",\"language\":\"Python\","
                                + "\"stargazers_count\":4200,\"forks_count\":500,\"open_issues_count\":80,"
                                + "\"topics\":[\"agent\",\"llm\"],"
                                + "\"created_at\":\"2026-08-01T00:00:00Z\","
                                + "\"pushed_at\":\"2026-08-27T00:00:00Z\",\"default_branch\":\"main\"}]}")
                        .build()
        );
    }
}
