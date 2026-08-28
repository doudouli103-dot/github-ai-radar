package com.github.airadar.github.service;

import com.github.airadar.github.model.GithubRepositorySourceContext;
import com.github.airadar.repository.model.GithubRepository;

public class NoopGithubRepositorySourceCollector implements GithubRepositorySourceCollector {

    @Override
    public GithubRepositorySourceContext collect(GithubRepository repository) {
        return GithubRepositorySourceContext.empty();
    }
}
