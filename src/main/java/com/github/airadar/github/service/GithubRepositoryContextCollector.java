package com.github.airadar.github.service;

import com.github.airadar.github.model.GithubRepositoryContext;
import com.github.airadar.repository.model.GithubRepository;

public interface GithubRepositoryContextCollector {

    GithubRepositoryContext collect(GithubRepository repository);
}
