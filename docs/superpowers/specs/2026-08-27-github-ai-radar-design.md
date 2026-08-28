# GitHub AI Radar Design

## 1. Project Positioning

Project name:

**GitHub AI Radar**

First-version positioning:

**Daily AI open source intelligence agent**

One-sentence goal:

Automatically discover fast-growing AI repositories on GitHub every day, score and filter them, analyze the Top 10, deeply inspect the Top 3 source code, and generate a Markdown daily technical intelligence report.

This project is not a simple GitHub Trending crawler. It is an engineering-oriented open source intelligence agent focused on answering:

- Which AI projects are heating up today?
- Why are they growing?
- What technical direction do they represent?
- Which projects are worth reading deeply?
- Which source files should be studied first?

## 2. First-Version Scope

The MVP must finish one complete daily loop:

1. Search and collect GitHub candidate repositories.
2. Save repository metadata.
3. Save daily repository snapshots.
4. Calculate HotScore from daily growth and activity.
5. Select AI-related Top 10 repositories.
6. Run lightweight LLM analysis for Top 10.
7. Run source-level LLM analysis for Top 3.
8. Generate one Markdown report.
9. Run automatically through scheduler.

The first version should not include:

- Dashboard
- Complex frontend
- Multi-agent platform
- Vector database
- Historical RAG question answering
- Complex user system or permission model
- Full repository ingestion
- Full codebase indexing

Success criterion:

Every day, the system can automatically generate a usable `GitHub AI Daily` report containing 10 AI projects, with deep source analysis for 3 of them.

## 3. Technology Choice

The main application should use Java + Spring Boot.

Reason:

The core of this project is long-running engineering orchestration, not model training or notebook experimentation. Java is responsible for stable backend responsibilities:

- GitHub API integration
- Scheduler
- PostgreSQL persistence
- Repository snapshots
- HotScore calculation
- Task orchestration
- AI Gateway calls
- Report generation
- Retry and failure recording

Python is optional and auxiliary. It can be introduced later for:

- Embedding
- Special text processing
- Model experiments
- RAG toolchain
- Python-native AI libraries

The boundary is:

```text
Spring Boot = system backbone
Python = optional AI capability plugin
```

Local device positioning:

| Device | Configuration | Recommended Role |
| --- | --- | --- |
| MacBook Pro | 24G / 1T | Development terminal, control console, debugging entry |
| Mac Studio | 128G / 1T | Model capability layer: LLM, Embedding, Reranker |
| Windows | 16G / 1T SSD / NVIDIA 4G | Data service layer: MySQL, Redis, Elasticsearch, Chroma, RAG API |

Runtime boundary:

```text
MacBook Pro runs development and control.
Mac Studio exposes model capability endpoints.
Windows exposes data and retrieval service endpoints.
Spring Boot remains the daily job owner.
```

## 4. Architecture

MVP architecture:

```text
GitHub
  |
  v
GitHub Collector
  |
  v
Repository Store
  |
  v
Daily Snapshot
  |
  v
HotScore Engine
  |
  v
Top 10 Lightweight Analysis
  |
  v
Top 3 Source Analysis
  |
  v
Markdown Report Generator
  |
  v
PostgreSQL + Local Report File
```

AI model access must go through AI Gateway:

```text
GitHub AI Radar
  |
  v
AI Gateway
  |
  +--> coder model
  |      source code / architecture / key module analysis
  |
  +--> general model
         project value / trend / innovation / business judgment
```

The application must not bind directly to a concrete model provider.

## 5. Core Modules

### github-collector

Responsibilities:

- Call GitHub Search API.
- Fetch repository metadata.
- Fetch README.
- Fetch topics.
- Fetch recent commits.
- Fetch recent issues or pull requests.
- Fetch Git Tree for Top 3 source analysis.

Candidate search should combine multiple queries, for example:

- `topic:ai`
- `topic:agent`
- `topic:rag`
- `topic:llm`
- `topic:mcp`
- `topic:code-generation`
- `created:>recent-date`
- `pushed:>recent-date`

### repository-store

Responsibilities:

- Upsert repository identity and stable metadata.
- Save daily snapshots.
- Avoid duplicate repositories across different search queries.

### hot-score-engine

Responsibilities:

- Compare today's snapshot with yesterday's snapshot.
- Calculate growth and activity metrics.
- Produce a normalized HotScore.

Initial score weights:

| Metric | Weight |
| --- | ---: |
| Star Growth | 35% |
| Fork Growth | 15% |
| Commit Activity | 15% |
| Issue Activity | 10% |
| AI Relevance | 15% |
| Freshness | 10% |

The system should prefer fast-growing repositories over permanently famous repositories.

Example:

```text
Yesterday: 3,000 stars
Today:     4,200 stars
Growth:   +1,200
```

This is more important than a mature repository with many total stars but low current growth.

### light-analysis

Responsibilities:

- Analyze Top 10 repositories.
- Use README, description, topics, recent commits, recent issues, and detected technology stack.
- Produce structured analysis.

Questions to answer:

- What problem does it solve?
- Why is it growing recently?
- Which category does it belong to: Agent, RAG, MCP, Coding, Workflow, Model Infra, Data, DevTool, or Other?
- What is the technical innovation?
- Does it have commercial value?
- Is it worth further research?

### code-analysis

Responsibilities:

- Analyze Top 3 repositories only.
- Use Git Tree to identify likely core directories.
- Select 10 to 30 key source files.
- Ask coder model to analyze architecture and implementation.

Focus areas:

- Agent Loop
- Tool Calling
- Memory
- Context Management
- RAG
- MCP
- Workflow
- Planning
- Model Router
- Multi-Agent

The system must not send the whole repository to the model.

### report-generator

Responsibilities:

- Generate one Markdown report per day.
- Save report content to database.
- Optionally save a local `.md` file for reading and sharing.

Report title:

```text
GitHub AI Daily - yyyy-MM-dd
```

Report sections:

- Today's trend summary
- Top 10 project table
- Top 3 deep analysis
- Today's most worth-studying project
- Suggested source files to read
- Tags and category distribution

## 6. Data Model

The MVP needs four tables.

### github_repository

Stores stable repository identity and metadata.

Important fields:

- id
- github_id
- owner
- name
- full_name
- html_url
- description
- language
- topics
- created_at
- pushed_at
- default_branch
- archived
- disabled
- first_seen_at
- last_seen_at

Unique key:

- github_id

### repository_snapshot

Stores daily metrics.

Important fields:

- id
- repository_id
- snapshot_date
- stars
- forks
- open_issues
- watchers
- commit_count_recent
- issue_count_recent
- pr_count_recent
- pushed_at
- collected_at

Unique key:

- repository_id + snapshot_date

### repository_analysis

Stores LLM analysis output.

Important fields:

- id
- repository_id
- analysis_date
- analysis_type
- model_role
- category
- summary
- growth_reason
- technical_innovation
- business_value
- learning_value
- key_files
- raw_output
- created_at

Suggested `analysis_type` values:

- `LIGHT_TOP10`
- `DEEP_SOURCE_TOP3`

### daily_report

Stores generated daily report.

Important fields:

- id
- report_date
- title
- markdown_content
- top_repository_ids
- trend_summary
- created_at

Unique key:

- report_date

## 7. Daily Workflow

Recommended workflow:

```text
01 collect candidate repositories
02 upsert repositories
03 collect daily snapshots
04 calculate HotScore
05 select Top 10 AI repositories
06 run lightweight LLM analysis
07 select Top 3 for source analysis
08 fetch Git Tree and key files
09 run coder model analysis
10 generate Markdown report
11 save report
```

The workflow should be implemented as a clear application service, not hidden inside scheduler code.

Suggested service:

```text
DailyRadarJobService.run(LocalDate reportDate)
```

The scheduler should only trigger this service.

## 8. Suggested Java Package Structure

```text
com.example.githubairadar
  config
  github
    client
    dto
  repository
    entity
    mapper
    service
  snapshot
    entity
    service
  scoring
    model
    service
  analysis
    gateway
    prompt
    service
    model
  report
    service
    template
  job
    service
    scheduler
  common
    exception
    util
```

## 9. AI Gateway Contract

The Java application calls AI Gateway through a stable interface:

```java
public interface AiGatewayClient {
    AiResponse chat(AiRequest request);
}
```

Suggested request fields:

- taskType
- modelRole
- systemPrompt
- userPrompt
- temperature
- responseFormat
- maxTokens

Suggested model roles:

- `coder`
- `general`

The business code should depend on `modelRole`, not on model names.

## 10. Prompt Output

LLM output should be structured JSON first, then converted into Markdown.

Top 10 lightweight analysis output:

```json
{
  "category": "Agent",
  "summary": "...",
  "problemSolved": "...",
  "growthReason": "...",
  "technicalInnovation": "...",
  "businessValue": "...",
  "learningValue": "...",
  "worthFurtherStudy": true
}
```

Top 3 source analysis output:

```json
{
  "architectureSummary": "...",
  "coreModules": ["..."],
  "keyFiles": [
    {
      "path": "...",
      "reason": "..."
    }
  ],
  "agentLoop": "...",
  "toolCalling": "...",
  "memory": "...",
  "contextManagement": "...",
  "rag": "...",
  "mcp": "...",
  "workflow": "...",
  "learningSuggestions": ["..."]
}
```

## 11. Error Handling

Required MVP behavior:

- GitHub API failure should be retried with backoff.
- Rate limit should be recorded clearly.
- A single repository analysis failure must not stop the whole daily report.
- Failed analysis should be marked in `repository_analysis`.
- The report should still be generated if at least part of Top 10 analysis succeeds.
- Scheduler should log every run start, end, duration, and failure reason.

## 12. Testing Strategy

MVP tests should cover:

- HotScore calculation
- Repository upsert logic
- Snapshot comparison
- Top 10 selection
- Top 3 selection
- Markdown report rendering
- AI Gateway mock response parsing

External GitHub API and AI Gateway calls should be tested through mocks or stub clients first.

## 13. Development Order

Strict first-version development order:

1. Spring Boot project skeleton.
2. PostgreSQL schema and migration.
3. GitHub Search API client.
4. Repository upsert.
5. Daily snapshot collection.
6. HotScore calculation.
7. Top 10 selection.
8. AI Gateway client abstraction.
9. Top 10 lightweight analysis.
10. Top 3 Git Tree and key file selection.
11. Top 3 source analysis.
12. Markdown report generation.
13. Scheduler.
14. End-to-end local run command.

## 14. Future Extensions

After one to three months of daily reports, add knowledge base features:

```text
Daily Reports
  |
  v
BGE-M3
  |
  v
Qdrant or pgvector
  |
  v
AI Project Knowledge Base
  |
  v
RAG Agent
```

Future questions:

- What changed in Agent technology in the last 30 days?
- Which Coding Agent projects grew fastest recently?
- Which projects use MCP?
- Which projects look similar to Claude Code?
- What are the 20 most worth-studying AI projects in the last three months?

These are explicitly not MVP requirements.
