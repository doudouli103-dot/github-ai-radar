package com.github.airadar.analysis.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(AiGatewayClient.class)
public class NoopAiGatewayClient implements AiGatewayClient {

    @Override
    public AiResponse chat(AiRequest request) {
        return new AiResponse("Unknown",
                "Fallback analysis for " + request.getUserPrompt()
                        + " because no real AI Gateway client is configured.");
    }
}
