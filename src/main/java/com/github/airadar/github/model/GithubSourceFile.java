package com.github.airadar.github.model;

public class GithubSourceFile {

    private final String path;
    private final String content;

    public GithubSourceFile(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }
}
