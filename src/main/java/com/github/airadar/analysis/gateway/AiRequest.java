package com.github.airadar.analysis.gateway;

public class AiRequest {

    private final String taskType;
    private final String modelRole;
    private final String systemPrompt;
    private final String userPrompt;

    public AiRequest(String taskType, String modelRole, String systemPrompt, String userPrompt) {
        this.taskType = taskType;
        this.modelRole = modelRole;
        this.systemPrompt = systemPrompt;
        this.userPrompt = userPrompt;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getModelRole() {
        return modelRole;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }
}
