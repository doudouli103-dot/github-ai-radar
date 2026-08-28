package com.github.airadar.analysis.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiCompatibleAiGatewayClientTest {

    @Test
    void callsChatCompletionsWithModelRoleAlias() {
        CapturingExchangeFunction exchange = new CapturingExchangeFunction();
        AiGatewayProperties properties = new AiGatewayProperties();
        properties.setBaseUrl("http://mac-studio.local:4000/v1");
        properties.setApiKey("sk-local");
        properties.getModels().setGeneral("gpt-oss-120b");
        properties.getModels().setCoder("qwen3-coder-next");

        OpenAiCompatibleAiGatewayClient client = new OpenAiCompatibleAiGatewayClient(
                WebClient.builder().exchangeFunction(exchange), properties);

        AiResponse response = client.chat(new AiRequest("DEEP_SOURCE_TOP3", "coder",
                "Analyze source", "owner/repo"));

        assertThat(response.getCategory()).isEqualTo("Unknown");
        assertThat(response.getContent()).isEqualTo("analysis result");
        assertThat(exchange.requests).hasSize(1);
        ClientRequest request = exchange.requests.get(0);
        assertThat(request.url().getPath()).endsWith("/chat/completions");
        assertThat(request.headers().getFirst("Authorization")).isEqualTo("Bearer sk-local");
    }

    @Test
    void resolvesGeneralRoleToGeneralModelAlias() {
        AiGatewayProperties properties = new AiGatewayProperties();
        properties.getModels().setGeneral("gpt-oss-120b");
        properties.getModels().setCoder("qwen3-coder-next");

        assertThat(properties.modelForRole("general")).isEqualTo("gpt-oss-120b");
        assertThat(properties.modelForRole("coder")).isEqualTo("qwen3-coder-next");
        assertThat(properties.modelForRole("unknown")).isEqualTo("gpt-oss-120b");
    }

    private static class CapturingExchangeFunction implements ExchangeFunction {

        private final List<ClientRequest> requests = new ArrayList<ClientRequest>();

        @Override
        public Mono<ClientResponse> exchange(ClientRequest request) {
            requests.add(request);
            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body("{\"choices\":[{\"message\":{\"content\":\"analysis result\"}}]}")
                    .build());
        }
    }
}
