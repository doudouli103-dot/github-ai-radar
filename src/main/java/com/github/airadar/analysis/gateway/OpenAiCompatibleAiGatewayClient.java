package com.github.airadar.analysis.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnExpression("'${ai-gateway.base-url:}' != ''")
public class OpenAiCompatibleAiGatewayClient implements AiGatewayClient {

    private final WebClient webClient;
    private final AiGatewayProperties properties;

    public OpenAiCompatibleAiGatewayClient(WebClient.Builder webClientBuilder,
                                           AiGatewayProperties properties) {
        WebClient.Builder builder = webClientBuilder.baseUrl(properties.getBaseUrl());
        if (properties.getApiKey() != null && !properties.getApiKey().trim().isEmpty()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey().trim());
        }
        this.webClient = builder.build();
        this.properties = properties;
    }

    OpenAiCompatibleAiGatewayClient(WebClient.Builder webClientBuilder,
                                    AiGatewayProperties properties,
                                    boolean testConstructor) {
        WebClient.Builder builder = webClientBuilder.baseUrl(properties.getBaseUrl());
        if (properties.getApiKey() != null && !properties.getApiKey().trim().isEmpty()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey().trim());
        }
        this.webClient = builder.build();
        this.properties = properties;
    }

    @Override
    public AiResponse chat(AiRequest request) {
        OpenAiChatResponse response = webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(toRequestBody(request))
                .retrieve()
                .bodyToMono(OpenAiChatResponse.class)
                .block();

        return new AiResponse("Unknown", extractContent(response));
    }

    private Map<String, Object> toRequestBody(AiRequest request) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("model", properties.modelForRole(request.getModelRole()));
        body.put("temperature", properties.getTemperature());
        body.put("max_tokens", properties.getMaxTokens());
        body.put("messages", messages(request));
        return body;
    }

    private List<Map<String, String>> messages(AiRequest request) {
        List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
        messages.add(message("system", request.getSystemPrompt()));
        messages.add(message("user", request.getUserPrompt()));
        return messages;
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> message = new HashMap<String, String>();
        message.put("role", role);
        message.put("content", content == null ? "" : content);
        return message;
    }

    private String extractContent(OpenAiChatResponse response) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "";
        }
        OpenAiChatResponse.Message message = response.getChoices().get(0).getMessage();
        return message == null || message.getContent() == null ? "" : message.getContent();
    }

    public static class OpenAiChatResponse {

        private List<Choice> choices = new ArrayList<Choice>();

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }

        public static class Choice {

            private Message message;

            public Message getMessage() {
                return message;
            }

            public void setMessage(Message message) {
                this.message = message;
            }
        }

        public static class Message {

            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
