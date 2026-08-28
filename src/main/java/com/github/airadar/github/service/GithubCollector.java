package com.github.airadar.github.service;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.snapshot.model.RepositorySnapshot;

import java.time.LocalDate;
import java.util.List;

public interface GithubCollector {

    List<GithubRepository> collectCandidates(LocalDate reportDate);

    RepositorySnapshot collectTodaySnapshot(GithubRepository repository, LocalDate reportDate);

    RepositorySnapshot findYesterdaySnapshot(GithubRepository repository, LocalDate reportDate);
}
