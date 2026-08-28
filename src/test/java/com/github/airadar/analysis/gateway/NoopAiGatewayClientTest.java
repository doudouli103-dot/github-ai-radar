package com.github.airadar.analysis.gateway;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoopAiGatewayClientTest {

    @Test
    void returnsDeterministicFallbackAnalysis() {
        NoopAiGatewayClient client = new NoopAiGatewayClient();

        AiResponse response = client.chat(new AiRequest("LIGHT_TOP10", "general",
                "Analyze repository", "owner/repo"));

        assertThat(response.getCategory()).isEqualTo("Unknown");
        assertThat(response.getContent()).contains("owner/repo");
    }
}
