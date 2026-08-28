package com.github.airadar.analysis.gateway;

public class AiResponse {

    private final String category;
    private final String content;

    public AiResponse(String category, String content) {
        this.category = category;
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }
}
