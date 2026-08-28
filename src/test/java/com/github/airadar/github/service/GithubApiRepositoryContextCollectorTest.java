package com.github.airadar.github.service;

import com.github.airadar.github.model.GithubRepositoryContext;
import com.github.airadar.repository.model.GithubRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GithubApiRepositoryContextCollectorTest {

    @Test
    void collectsReadmeRecentCommitsAndIssues() {
        CapturingExchangeFunction exchange = new CapturingExchangeFunction();
        GithubApiRepositoryContextCollector collector = new GithubApiRepositoryContextCollector(
                WebClient.builder().baseUrl("https://api.github.com").exchangeFunction(exchange).build());
        GithubRepository repository = new GithubRepository(1L, 100L, "owner", "agent",
                "owner/agent", "https://github.com/owner/agent", "AI agent framework",
                "Python", Arrays.asList("agent"), OffsetDateTime.now(), OffsetDateTime.now(),
                "main", 100, 10, 5);

        GithubRepositoryContext context = collector.collect(repository);

        assertThat(context.getReadme()).contains("Agent framework README");
        assertThat(context.getRecentCommits()).containsExactly("Add tool calling loop");
        assertThat(context.getRecentIssues()).containsExactly("Support MCP server");
        assertThat(exchange.paths).containsExactly(
                "/repos/owner/agent/readme",
                "/repos/owner/agent/commits",
                "/repos/owner/agent/issues"
        );
    }

    private static class CapturingExchangeFunction implements ExchangeFunction {

        private final List<String> paths = new ArrayList<String>();

        @Override
        public Mono<ClientResponse> exchange(ClientRequest request) {
            paths.add(request.url().getPath());
            if (request.url().getPath().endsWith("/readme")) {
                String encoded = Base64.getEncoder().encodeToString("Agent framework README".getBytes());
                return ok("{\"content\":\"" + encoded + "\",\"encoding\":\"base64\"}");
            }
            if (request.url().getPath().endsWith("/commits")) {
                return ok("[{\"commit\":{\"message\":\"Add tool calling loop\"}}]");
            }
            return ok("[{\"title\":\"Support MCP server\"}]");
        }

        private Mono<ClientResponse> ok(String body) {
            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .build());
        }
    }
}
