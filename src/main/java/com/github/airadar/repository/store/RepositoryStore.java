package com.github.airadar.repository.store;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.snapshot.model.RepositorySnapshot;

import java.time.LocalDate;
import java.util.Optional;

public interface RepositoryStore {

    GithubRepository upsertRepository(GithubRepository repository);

    void saveSnapshot(RepositorySnapshot snapshot);

    Optional<RepositorySnapshot> findSnapshot(Long repositoryId, LocalDate snapshotDate);
}
