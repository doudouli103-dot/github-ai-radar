CREATE TABLE github_repository (
    id BIGSERIAL PRIMARY KEY,
    github_id BIGINT NOT NULL,
    owner VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    full_name VARCHAR(512) NOT NULL,
    html_url TEXT NOT NULL,
    description TEXT,
    language VARCHAR(128),
    topics JSONB,
    created_at TIMESTAMPTZ,
    pushed_at TIMESTAMPTZ,
    default_branch VARCHAR(255),
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    disabled BOOLEAN NOT NULL DEFAULT FALSE,
    first_seen_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_seen_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_github_repository_github_id UNIQUE (github_id)
);

CREATE TABLE repository_snapshot (
    id BIGSERIAL PRIMARY KEY,
    repository_id BIGINT NOT NULL REFERENCES github_repository(id),
    snapshot_date DATE NOT NULL,
    stars INTEGER NOT NULL DEFAULT 0,
    forks INTEGER NOT NULL DEFAULT 0,
    open_issues INTEGER NOT NULL DEFAULT 0,
    watchers INTEGER NOT NULL DEFAULT 0,
    commit_count_recent INTEGER NOT NULL DEFAULT 0,
    issue_count_recent INTEGER NOT NULL DEFAULT 0,
    pr_count_recent INTEGER NOT NULL DEFAULT 0,
    pushed_at TIMESTAMPTZ,
    collected_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_repository_snapshot_repo_date UNIQUE (repository_id, snapshot_date)
);

CREATE TABLE repository_analysis (
    id BIGSERIAL PRIMARY KEY,
    repository_id BIGINT NOT NULL REFERENCES github_repository(id),
    analysis_date DATE NOT NULL,
    analysis_type VARCHAR(64) NOT NULL,
    model_role VARCHAR(64) NOT NULL,
    category VARCHAR(128),
    summary TEXT,
    growth_reason TEXT,
    technical_innovation TEXT,
    business_value TEXT,
    learning_value TEXT,
    key_files JSONB,
    raw_output JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_repository_analysis_date_type
    ON repository_analysis (analysis_date, analysis_type);

CREATE TABLE daily_report (
    id BIGSERIAL PRIMARY KEY,
    report_date DATE NOT NULL,
    title VARCHAR(255) NOT NULL,
    markdown_content TEXT NOT NULL,
    top_repository_ids JSONB,
    trend_summary TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_daily_report_report_date UNIQUE (report_date)
);
