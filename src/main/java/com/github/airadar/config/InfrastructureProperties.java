package com.github.airadar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "infrastructure")
public class InfrastructureProperties {

    private Device macbook = new Device();
    private MacStudio macStudio = new MacStudio();
    private Windows windows = new Windows();

    public Device getMacbook() {
        return macbook;
    }

    public void setMacbook(Device macbook) {
        this.macbook = macbook;
    }

    public MacStudio getMacStudio() {
        return macStudio;
    }

    public void setMacStudio(MacStudio macStudio) {
        this.macStudio = macStudio;
    }

    public Windows getWindows() {
        return windows;
    }

    public void setWindows(Windows windows) {
        this.windows = windows;
    }

    public static class Device {

        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class MacStudio extends Device {

        private String modelGatewayUrl;
        private String embeddingUrl;
        private String rerankerUrl;

        public String getModelGatewayUrl() {
            return modelGatewayUrl;
        }

        public void setModelGatewayUrl(String modelGatewayUrl) {
            this.modelGatewayUrl = modelGatewayUrl;
        }

        public String getEmbeddingUrl() {
            return embeddingUrl;
        }

        public void setEmbeddingUrl(String embeddingUrl) {
            this.embeddingUrl = embeddingUrl;
        }

        public String getRerankerUrl() {
            return rerankerUrl;
        }

        public void setRerankerUrl(String rerankerUrl) {
            this.rerankerUrl = rerankerUrl;
        }
    }

    public static class Windows extends Device {

        private String mysqlUrl;
        private String redisUrl;
        private String elasticsearchUrl;
        private String chromaUrl;
        private String ragApiUrl;

        public String getMysqlUrl() {
            return mysqlUrl;
        }

        public void setMysqlUrl(String mysqlUrl) {
            this.mysqlUrl = mysqlUrl;
        }

        public String getRedisUrl() {
            return redisUrl;
        }

        public void setRedisUrl(String redisUrl) {
            this.redisUrl = redisUrl;
        }

        public String getElasticsearchUrl() {
            return elasticsearchUrl;
        }

        public void setElasticsearchUrl(String elasticsearchUrl) {
            this.elasticsearchUrl = elasticsearchUrl;
        }

        public String getChromaUrl() {
            return chromaUrl;
        }

        public void setChromaUrl(String chromaUrl) {
            this.chromaUrl = chromaUrl;
        }

        public String getRagApiUrl() {
            return ragApiUrl;
        }

        public void setRagApiUrl(String ragApiUrl) {
            this.ragApiUrl = ragApiUrl;
        }
    }
}
