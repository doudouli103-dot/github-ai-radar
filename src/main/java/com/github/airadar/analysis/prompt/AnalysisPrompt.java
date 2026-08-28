package com.github.airadar.analysis.prompt;

public class AnalysisPrompt {

    private final String systemPrompt;
    private final String userPrompt;

    public AnalysisPrompt(String systemPrompt, String userPrompt) {
        this.systemPrompt = systemPrompt;
        this.userPrompt = userPrompt;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }
}
