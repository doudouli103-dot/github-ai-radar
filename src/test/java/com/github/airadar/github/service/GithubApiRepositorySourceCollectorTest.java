package com.github.airadar.github.service;

import com.github.airadar.github.model.GithubRepositorySourceContext;
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

class GithubApiRepositorySourceCollectorTest {

    @Test
    void collectsSelectedSourceFilesFromGitTree() {
        CapturingExchangeFunction exchange = new CapturingExchangeFunction();
        GithubApiRepositorySourceCollector collector = new GithubApiRepositorySourceCollector(
                WebClient.builder().baseUrl("https://api.github.com").exchangeFunction(exchange).build(),
                new GithubSourceFileSelector());
        GithubRepository repository = new GithubRepository(1L, 100L, "owner", "agent",
                "owner/agent", "https://github.com/owner/agent", "AI agent framework",
                "Python", Arrays.asList("agent"), OffsetDateTime.now(), OffsetDateTime.now(),
                "main", 100, 10, 5);

        GithubRepositorySourceContext context = collector.collect(repository);

        assertThat(context.getFiles()).hasSize(2);
        assertThat(context.getFiles().get(0).getPath()).isEqualTo("src/agent/loop.py");
        assertThat(context.getFiles().get(0).getContent()).contains("class AgentLoop");
        assertThat(context.getFiles().get(1).getPath()).isEqualTo("src/rag/retriever.py");
        assertThat(exchange.paths).containsExactly(
                "/repos/owner/agent/git/trees/main",
                "/repos/owner/agent/contents/src/agent/loop.py",
                "/repos/owner/agent/contents/src/rag/retriever.py"
        );
    }

    private static class CapturingExchangeFunction implements ExchangeFunction {

        private final List<String> paths = new ArrayList<String>();

        @Override
        public Mono<ClientResponse> exchange(ClientRequest request) {
            paths.add(request.url().getPath());
            if (request.url().getPath().endsWith("/git/trees/main")) {
                return ok("{\"tree\":["
                        + "{\"path\":\"src/agent/loop.py\",\"type\":\"blob\",\"sha\":\"a\",\"size\":1000},"
                        + "{\"path\":\"docs/guide.md\",\"type\":\"blob\",\"sha\":\"b\",\"size\":1000},"
                        + "{\"path\":\"src/rag/retriever.py\",\"type\":\"blob\",\"sha\":\"c\",\"size\":1000}"
                        + "]}");
            }
            if (request.url().getPath().endsWith("/src/agent/loop.py")) {
                return content("class AgentLoop:\n    pass\n");
            }
            return content("class Retriever:\n    pass\n");
        }

        private Mono<ClientResponse> content(String value) {
            String encoded = Base64.getEncoder().encodeToString(value.getBytes());
            return ok("{\"content\":\"" + encoded + "\",\"encoding\":\"base64\"}");
        }

        private Mono<ClientResponse> ok(String body) {
            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .build());
        }
    }
}
