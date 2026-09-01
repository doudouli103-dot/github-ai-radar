# GitHub AI Radar

Daily AI open source intelligence agent.

The MVP collects AI-related GitHub repositories, stores daily snapshots, calculates HotScore, analyzes Top 10 projects, runs source-level analysis for Top 3 projects, and generates a Markdown daily report.

## Local Development

```bash
mvn test
```

The first implementation keeps external GitHub and AI Gateway calls behind interfaces so the core workflow can be tested without credentials.

## Web UI

The thin intelligence console is maintained as a separate frontend repository:

```text
https://github.com/doudouli103-dot/github-ai-radar_webui
```

If the frontend repository is cloned next to this backend workspace, start it with:

```bash
cd github-ai-radar_webui
npm install
npm run dev
```

The Web UI uses Vue 3, Vite, TypeScript, and a small API adapter. It first tries:

```text
GET /api/reports/latest
```

If the backend API is not available yet, it falls back to local mock data so the console remains usable while the Spring Boot API is being developed.

## GitHub Collection

`GithubApiRepositoryCollector` calls GitHub REST Search API:

```text
GET https://api.github.com/search/repositories?q=topic:{topic} pushed:>{date} stars:>{minStars}&sort=stars&order=desc&per_page=30
```

Default topics are configured in `src/main/resources/application.yml`:

```text
ai, llm, agent, rag, mcp, code-generation, generative-ai
```

Set `GITHUB_TOKEN` to increase rate limits:

```bash
export GITHUB_TOKEN=your_token
```

`GithubApiRepositoryContextCollector` enriches Top 10 analysis with:

```text
GET /repos/{owner}/{repo}/readme
GET /repos/{owner}/{repo}/commits?per_page=5
GET /repos/{owner}/{repo}/issues?state=open&per_page=5
```

README content is decoded from GitHub's Base64 response. Commit messages and issue titles are passed into the lightweight analysis prompt as recent activity signals.

`GithubApiRepositorySourceCollector` enriches Top 3 source-level analysis with:

```text
GET /repos/{owner}/{repo}/git/trees/{defaultBranch}?recursive=1
GET /repos/{owner}/{repo}/contents/{path}?ref={defaultBranch}
```

`GithubSourceFileSelector` chooses up to 30 likely core source files, prioritizing Agent, Tool Calling, RAG, MCP, Workflow, Router, and core implementation paths while skipping tests, docs, examples, generated files, and build output. File content is decoded from GitHub's Base64 response and truncated before being passed to the coder prompt.

If no real AI Gateway client is configured, `NoopAiGatewayClient` returns deterministic fallback analysis so the local workflow remains runnable.

## AI Gateway

`OpenAiCompatibleAiGatewayClient` is enabled when `ai-gateway.base-url` is set. It calls:

```text
POST {AI_GATEWAY_BASE_URL}/chat/completions
```

Recommended Mac Studio configuration:

```bash
export AI_GATEWAY_BASE_URL=http://macstudio.tentest.cn:8088/v1
export AI_GATEWAY_API_KEY=local-dev-key
export AI_GATEWAY_GENERAL_MODEL=gpt-oss-120b
export AI_GATEWAY_CODER_MODEL=qwen3-coder-next
```

Business code uses `modelRole` only:

```text
general -> AI_GATEWAY_GENERAL_MODEL
coder   -> AI_GATEWAY_CODER_MODEL
```

## Structured Analysis

`AnalysisPromptService` builds separate prompts for:

```text
LIGHT_TOP10      -> general model
DEEP_SOURCE_TOP3 -> coder model
```

Both prompts ask the model to return valid JSON. `RepositoryAnalysisParser` parses that JSON into `RepositoryAnalysis` fields before persistence and report rendering. If the model returns plain text, the parser falls back to an `Unknown` category and stores the raw text safely.

## Persistence

`RepositoryStore` is the persistence boundary used by the daily job.

Current implementation:

```text
JdbcRepositoryStore
  -> upsert github_repository by github_id
  -> save repository_snapshot by repository_id + snapshot_date
  -> read yesterday snapshot for HotScore growth calculation

JdbcAnalysisStore
  -> save repository_analysis for Top 10 lightweight analysis
  -> save repository_analysis for Top 3 source analysis

JdbcDailyReportStore
  -> upsert daily_report by report_date
```

The SQL implementation currently targets the PostgreSQL migration under `src/main/resources/db/migration`.

## LAN Deployment Topology

Use the shared LAN hostnames:

```text
192.168.1.101  macbook.tentest.cn
192.168.1.102  macstudio.tentest.cn
192.168.1.103  windows.tentest.cn
```

Deploy this backend on Windows. Configure AI Gateway calls to Mac Studio:

```bash
export AI_GATEWAY_BASE_URL=http://macstudio.tentest.cn:8088/v1
export AI_GATEWAY_API_KEY=local-dev-key
```

Windows deployment values are available in `.env.windows.example`.

```text
MacBook Pro -> development terminal and debugging entry
Mac Studio  -> model capability layer: LLM, Embedding, Reranker
Windows     -> data service layer: MySQL, Redis, Elasticsearch, Chroma, RAG API
```

See `docs/architecture/local-device-topology.md` for endpoint environment variables and boundary decisions.
