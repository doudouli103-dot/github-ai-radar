package com.github.airadar.github.service;

import com.github.airadar.github.model.GithubRepositoryContext;
import com.github.airadar.repository.model.GithubRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(GithubRepositoryContextCollector.class)
public class NoopGithubRepositoryContextCollector implements GithubRepositoryContextCollector {

    @Override
    public GithubRepositoryContext collect(GithubRepository repository) {
        return GithubRepositoryContext.empty();
    }
}
