package com.github.airadar.repository.store;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@ConditionalOnMissingBean(RepositoryStore.class)
public class NoopRepositoryStore implements RepositoryStore {

    @Override
    public GithubRepository upsertRepository(GithubRepository repository) {
        return repository;
    }

    @Override
    public void saveSnapshot(RepositorySnapshot snapshot) {
    }

    @Override
    public Optional<RepositorySnapshot> findSnapshot(Long repositoryId, LocalDate snapshotDate) {
        return Optional.empty();
    }
}
