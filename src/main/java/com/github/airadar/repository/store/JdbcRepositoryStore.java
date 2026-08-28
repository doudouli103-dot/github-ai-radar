package com.github.airadar.repository.store;

import com.github.airadar.repository.model.GithubRepository;
import com.github.airadar.snapshot.model.RepositorySnapshot;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcRepositoryStore implements RepositoryStore {

    private final NamedParameterJdbcOperations jdbc;

    public JdbcRepositoryStore(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public GithubRepository upsertRepository(GithubRepository repository) {
        String sql = "INSERT INTO github_repository (github_id, owner, name, full_name, html_url, "
                + "description, language, topics, created_at, pushed_at, default_branch, last_seen_at) "
                + "VALUES (:githubId, :owner, :name, :fullName, :htmlUrl, :description, :language, "
                + "CAST(:topics AS jsonb), :createdAt, :pushedAt, :defaultBranch, NOW()) "
                + "ON CONFLICT (github_id) DO UPDATE SET "
                + "owner = EXCLUDED.owner, name = EXCLUDED.name, full_name = EXCLUDED.full_name, "
                + "html_url = EXCLUDED.html_url, description = EXCLUDED.description, "
                + "language = EXCLUDED.language, topics = EXCLUDED.topics, "
                + "pushed_at = EXCLUDED.pushed_at, default_branch = EXCLUDED.default_branch, "
                + "last_seen_at = NOW() RETURNING id";

        Long id = jdbc.queryForObject(sql, repositoryParams(repository), Long.class);
        return new GithubRepository(id, repository.getGithubId(), repository.getOwner(), repository.getName(),
                repository.getFullName(), repository.getHtmlUrl(), repository.getDescription(),
                repository.getLanguage(), repository.getTopics(), repository.getCreatedAt(),
                repository.getPushedAt(), repository.getDefaultBranch(), repository.getStars(),
                repository.getForks(), repository.getOpenIssues());
    }

    @Override
    public void saveSnapshot(RepositorySnapshot snapshot) {
        String sql = "INSERT INTO repository_snapshot (repository_id, snapshot_date, stars, forks, "
                + "open_issues, commit_count_recent, issue_count_recent, pr_count_recent) "
                + "VALUES (:repositoryId, :snapshotDate, :stars, :forks, :openIssues, "
                + ":commitCountRecent, :issueCountRecent, :prCountRecent) "
                + "ON CONFLICT (repository_id, snapshot_date) DO UPDATE SET "
                + "stars = EXCLUDED.stars, forks = EXCLUDED.forks, open_issues = EXCLUDED.open_issues, "
                + "commit_count_recent = EXCLUDED.commit_count_recent, "
                + "issue_count_recent = EXCLUDED.issue_count_recent, "
                + "pr_count_recent = EXCLUDED.pr_count_recent, collected_at = NOW()";
        jdbc.update(sql, snapshotParams(snapshot));
    }

    @Override
    public Optional<RepositorySnapshot> findSnapshot(Long repositoryId, LocalDate snapshotDate) {
        String sql = "SELECT repository_id, snapshot_date, stars, forks, open_issues, "
                + "commit_count_recent, issue_count_recent, pr_count_recent "
                + "FROM repository_snapshot "
                + "WHERE repository_id = :repositoryId AND snapshot_date = :snapshotDate";
        List<RepositorySnapshot> snapshots = jdbc.query(sql,
                new MapSqlParameterSource()
                        .addValue("repositoryId", repositoryId)
                        .addValue("snapshotDate", snapshotDate),
                new RepositorySnapshotRowMapper());
        if (snapshots.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(snapshots.get(0));
    }

    private MapSqlParameterSource repositoryParams(GithubRepository repository) {
        return new MapSqlParameterSource()
                .addValue("githubId", repository.getGithubId())
                .addValue("owner", repository.getOwner())
                .addValue("name", repository.getName())
                .addValue("fullName", repository.getFullName())
                .addValue("htmlUrl", repository.getHtmlUrl())
                .addValue("description", repository.getDescription())
                .addValue("language", repository.getLanguage())
                .addValue("topics", toJsonArray(repository.getTopics()))
                .addValue("createdAt", repository.getCreatedAt())
                .addValue("pushedAt", repository.getPushedAt())
                .addValue("defaultBranch", repository.getDefaultBranch());
    }

    private MapSqlParameterSource snapshotParams(RepositorySnapshot snapshot) {
        return new MapSqlParameterSource()
                .addValue("repositoryId", snapshot.getRepositoryId())
                .addValue("snapshotDate", snapshot.getSnapshotDate())
                .addValue("stars", snapshot.getStars())
                .addValue("forks", snapshot.getForks())
                .addValue("openIssues", snapshot.getOpenIssues())
                .addValue("commitCountRecent", snapshot.getCommitCountRecent())
                .addValue("issueCountRecent", snapshot.getIssueCountRecent())
                .addValue("prCountRecent", snapshot.getPrCountRecent());
    }

    private String toJsonArray(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(values.get(i).replace("\"", "\\\"")).append("\"");
        }
        json.append("]");
        return json.toString();
    }

    private static class RepositorySnapshotRowMapper implements RowMapper<RepositorySnapshot> {
        @Override
        public RepositorySnapshot mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new RepositorySnapshot(rs.getLong("repository_id"),
                    rs.getDate("snapshot_date").toLocalDate(),
                    rs.getInt("stars"),
                    rs.getInt("forks"),
                    rs.getInt("open_issues"),
                    rs.getInt("commit_count_recent"),
                    rs.getInt("issue_count_recent"),
                    rs.getInt("pr_count_recent"));
        }
    }
}
