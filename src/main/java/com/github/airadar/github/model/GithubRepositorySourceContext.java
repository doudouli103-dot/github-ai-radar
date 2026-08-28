package com.github.airadar.github.model;

import java.util.Collections;
import java.util.List;

public class GithubRepositorySourceContext {

    private final List<GithubSourceFile> files;

    public GithubRepositorySourceContext(List<GithubSourceFile> files) {
        this.files = files == null ? Collections.<GithubSourceFile>emptyList() : files;
    }

    public List<GithubSourceFile> getFiles() {
        return files;
    }

    public static GithubRepositorySourceContext empty() {
        return new GithubRepositorySourceContext(Collections.<GithubSourceFile>emptyList());
    }
}
