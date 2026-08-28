package com.github.airadar.github.service;

import com.github.airadar.github.model.GithubRepositorySourceContext;
import com.github.airadar.repository.model.GithubRepository;

public interface GithubRepositorySourceCollector {

    GithubRepositorySourceContext collect(GithubRepository repository);
}
