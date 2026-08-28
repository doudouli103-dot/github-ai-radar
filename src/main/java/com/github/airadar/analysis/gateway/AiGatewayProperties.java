package com.github.airadar.analysis.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai-gateway")
public class AiGatewayProperties {

    private String baseUrl;
    private String apiKey;
    private double temperature = 0.2;
    private int maxTokens = 4096;
    private Models models = new Models();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Models getModels() {
        return models;
    }

    public void setModels(Models models) {
        this.models = models;
    }

    public String modelForRole(String modelRole) {
        if ("coder".equalsIgnoreCase(modelRole)) {
            return models.getCoder();
        }
        return models.getGeneral();
    }

    public static class Models {

        private String general = "gpt-oss-120b";
        private String coder = "qwen3-coder-next";

        public String getGeneral() {
            return general;
        }

        public void setGeneral(String general) {
            this.general = general;
        }

        public String getCoder() {
            return coder;
        }

        public void setCoder(String coder) {
            this.coder = coder;
        }
    }
}
