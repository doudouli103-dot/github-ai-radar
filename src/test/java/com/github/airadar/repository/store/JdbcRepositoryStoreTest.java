package com.github.airadar.repository.store;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcRepositoryStoreTest {

    @Test
    void upsertsRepositoryAndReturnsDatabaseId() {
        NamedParameterJdbcOperations jdbc = mock(NamedParameterJdbcOperations.class);
        when(jdbc.queryForObject(any(String.class), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn(77L);
        JdbcRepositoryStore store = new JdbcRepositoryStore(jdbc);

        GithubRepository saved = store.upsertRepository(new GithubRepository(null, 100L,
                "owner", "agent", "owner/agent", "https://github.com/owner/agent",
                "AI agent framework", "Python", Arrays.asList("agent", "llm"),
                OffsetDateTime.parse("2026-08-01T00:00:00Z"),
                OffsetDateTime.parse("2026-08-27T00:00:00Z"), "main",
                4200, 500, 80));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbc).queryForObject(sqlCaptor.capture(), paramsCaptor.capture(), eq(Long.class));

        assertThat(saved.getId()).isEqualTo(77L);
        assertThat(saved.getGithubId()).isEqualTo(100L);
        assertThat(sqlCaptor.getValue()).contains("ON CONFLICT (github_id)");
        assertThat(paramsCaptor.getValue().getValue("githubId")).isEqualTo(100L);
        assertThat(paramsCaptor.getValue().getValue("fullName")).isEqualTo("owner/agent");
    }

    @Test
    void savesSnapshotByRepositoryAndDate() {
        NamedParameterJdbcOperations jdbc = mock(NamedParameterJdbcOperations.class);
        JdbcRepositoryStore store = new JdbcRepositoryStore(jdbc);

        store.saveSnapshot(new RepositorySnapshot(77L, LocalDate.of(2026, 8, 27),
                4200, 500, 80, 40, 30, 10));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbc).update(sqlCaptor.capture(), paramsCaptor.capture());

        assertThat(sqlCaptor.getValue()).contains("ON CONFLICT (repository_id, snapshot_date)");
        assertThat(paramsCaptor.getValue().getValue("repositoryId")).isEqualTo(77L);
        assertThat(paramsCaptor.getValue().getValue("stars")).isEqualTo(4200);
        assertThat(paramsCaptor.getValue().getValue("commitCountRecent")).isEqualTo(40);
    }
}
