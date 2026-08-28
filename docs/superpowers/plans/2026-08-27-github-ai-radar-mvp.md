# GitHub AI Radar MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a runnable Java Spring Boot MVP skeleton for the daily AI open source intelligence agent.

**Architecture:** Spring Boot owns scheduling, orchestration, persistence boundaries, scoring, AI Gateway abstraction, and Markdown report generation. GitHub and AI calls are represented by stable interfaces first, so the core workflow can be tested without external credentials.

**Tech Stack:** Java 8, Spring Boot 2.7.x, Maven, JUnit 5, PostgreSQL-compatible schema migration files.

## Global Constraints

- Main application uses Java + Spring Boot.
- Python is optional and auxiliary, not part of the MVP backbone.
- MVP includes repository collection boundaries, daily snapshots, HotScore, Top 10/Top 3 selection, AI Gateway abstraction, Markdown report generation, and scheduler.
- MVP excludes Dashboard, complex frontend, vector database, historical RAG, multi-agent platform, full repository ingestion, and full codebase indexing.
- External GitHub API and AI Gateway calls must be mockable or replaceable.

---

### Task 1: Project Skeleton

**Files:**
- Create: `pom.xml`
- Create: `README.md`
- Create: `src/main/java/com/github/airadar/GithubAiRadarApplication.java`
- Create: `src/main/resources/application.yml`

**Interfaces:**
- Produces: Maven project that can compile and run tests with `mvn test`.

- [x] **Step 1: Create Maven Spring Boot project files**
- [x] **Step 2: Add the application entrypoint**
- [x] **Step 3: Add local configuration placeholders**
- [x] **Step 4: Run `mvn test`**

### Task 2: Core Domain Models And HotScore

**Files:**
- Create: `src/main/java/com/github/airadar/repository/model/GithubRepository.java`
- Create: `src/main/java/com/github/airadar/snapshot/model/RepositorySnapshot.java`
- Create: `src/main/java/com/github/airadar/scoring/model/RepositoryScore.java`
- Create: `src/main/java/com/github/airadar/scoring/service/HotScoreService.java`
- Test: `src/test/java/com/github/airadar/scoring/service/HotScoreServiceTest.java`

**Interfaces:**
- Produces: `HotScoreService.score(GithubRepository repository, RepositorySnapshot today, RepositorySnapshot yesterday)`.

- [x] **Step 1: Write failing HotScore tests**
- [x] **Step 2: Run test and verify failure**
- [x] **Step 3: Implement models and scoring service**
- [x] **Step 4: Run test and verify pass**

### Task 3: Report Rendering

**Files:**
- Create: `src/main/java/com/github/airadar/analysis/model/RepositoryAnalysis.java`
- Create: `src/main/java/com/github/airadar/report/model/DailyReport.java`
- Create: `src/main/java/com/github/airadar/report/service/MarkdownReportService.java`
- Test: `src/test/java/com/github/airadar/report/service/MarkdownReportServiceTest.java`

**Interfaces:**
- Consumes: `GithubRepository`, `RepositoryScore`, `RepositoryAnalysis`.
- Produces: `MarkdownReportService.render(LocalDate reportDate, List<RepositoryScore> topScores, List<RepositoryAnalysis> analyses)`.

- [x] **Step 1: Write failing Markdown report test**
- [x] **Step 2: Run test and verify failure**
- [x] **Step 3: Implement report models and renderer**
- [x] **Step 4: Run test and verify pass**

### Task 4: Workflow Boundaries

**Files:**
- Create: `src/main/java/com/github/airadar/github/service/GithubCollector.java`
- Create: `src/main/java/com/github/airadar/analysis/gateway/AiGatewayClient.java`
- Create: `src/main/java/com/github/airadar/analysis/gateway/AiRequest.java`
- Create: `src/main/java/com/github/airadar/analysis/gateway/AiResponse.java`
- Create: `src/main/java/com/github/airadar/job/service/DailyRadarJobService.java`
- Create: `src/main/java/com/github/airadar/job/scheduler/DailyRadarScheduler.java`
- Test: `src/test/java/com/github/airadar/job/service/DailyRadarJobServiceTest.java`

**Interfaces:**
- Consumes: collector, scoring, analysis, report rendering interfaces.
- Produces: `DailyRadarJobService.run(LocalDate reportDate)`.

- [x] **Step 1: Write failing workflow orchestration test with fake collaborators**
- [x] **Step 2: Run test and verify failure**
- [x] **Step 3: Implement interfaces, job service, and scheduler shell**
- [x] **Step 4: Run test and verify pass**

### Task 5: Database Migration

**Files:**
- Create: `src/main/resources/db/migration/V1__init_github_ai_radar.sql`

**Interfaces:**
- Produces: PostgreSQL schema for `github_repository`, `repository_snapshot`, `repository_analysis`, and `daily_report`.

- [x] **Step 1: Add migration file**
- [x] **Step 2: Verify SQL has the four MVP tables and unique keys**
- [x] **Step 3: Run `mvn test` to ensure resource changes do not break build**

### Task 6: GitHub Search API Collector

**Files:**
- Create: `src/main/java/com/github/airadar/github/config/GithubSearchProperties.java`
- Create: `src/main/java/com/github/airadar/github/service/GithubSearchQueryFactory.java`
- Create: `src/main/java/com/github/airadar/github/dto/GithubSearchRepositoryItem.java`
- Create: `src/main/java/com/github/airadar/github/dto/GithubSearchResponse.java`
- Create: `src/main/java/com/github/airadar/github/service/GithubApiRepositoryCollector.java`
- Modify: `src/main/java/com/github/airadar/repository/model/GithubRepository.java`
- Test: `src/test/java/com/github/airadar/github/service/GithubSearchQueryFactoryTest.java`
- Test: `src/test/java/com/github/airadar/github/service/GithubApiRepositoryCollectorTest.java`

**Interfaces:**
- Consumes: `github.search.*` configuration.
- Produces: Real GitHub REST Search API collector using `/search/repositories`.

- [x] **Step 1: Write failing query factory and collector tests**
- [x] **Step 2: Run tests and verify failure**
- [x] **Step 3: Implement query factory, DTOs, WebClient collector, and repository field mapping**
- [x] **Step 4: Run targeted tests and full `mvn test`**

### Task 7: Default AI Gateway Fallback

**Files:**
- Create: `src/main/java/com/github/airadar/analysis/gateway/NoopAiGatewayClient.java`
- Test: `src/test/java/com/github/airadar/analysis/gateway/NoopAiGatewayClientTest.java`

**Interfaces:**
- Produces: `NoopAiGatewayClient` as a `@ConditionalOnMissingBean(AiGatewayClient.class)` fallback.

- [x] **Step 1: Write failing fallback client test**
- [x] **Step 2: Run test and verify failure**
- [x] **Step 3: Implement deterministic fallback AI Gateway client**
- [x] **Step 4: Run targeted tests and full `mvn test`**

### Task 8: Local Device Topology

**Files:**
- Create: `docs/architecture/local-device-topology.md`
- Create: `src/main/java/com/github/airadar/config/InfrastructureProperties.java`
- Modify: `src/main/resources/application.yml`
- Modify: `README.md`
- Modify: `docs/superpowers/specs/2026-08-27-github-ai-radar-design.md`
- Test: `src/test/java/com/github/airadar/config/InfrastructurePropertiesTest.java`

**Interfaces:**
- Consumes: MacBook Pro, Mac Studio, and Windows device-role decisions.
- Produces: Typed `infrastructure.*` Spring configuration for model and data service endpoints.

- [x] **Step 1: Write failing configuration binding test**
- [x] **Step 2: Run test and verify failure**
- [x] **Step 3: Implement `InfrastructureProperties` and local endpoint configuration**
- [x] **Step 4: Document local device topology and run full `mvn test`**

### Task 9: Repository Persistence Boundary

**Files:**
- Create: `src/main/java/com/github/airadar/repository/store/RepositoryStore.java`
- Create: `src/main/java/com/github/airadar/repository/store/JdbcRepositoryStore.java`
- Create: `src/main/java/com/github/airadar/repository/store/NoopRepositoryStore.java`
- Modify: `src/main/java/com/github/airadar/job/service/DailyRadarJobService.java`
- Modify: `src/test/java/com/github/airadar/job/service/DailyRadarJobServiceTest.java`
- Modify: `src/test/java/com/github/airadar/config/SpringBeanWiringTest.java`
- Test: `src/test/java/com/github/airadar/repository/store/JdbcRepositoryStoreTest.java`

**Interfaces:**
- Consumes: `GithubRepository` and `RepositorySnapshot`.
- Produces: `RepositoryStore.upsertRepository`, `RepositoryStore.saveSnapshot`, and `RepositoryStore.findSnapshot`.

- [x] **Step 1: Write failing JDBC persistence tests**
- [x] **Step 2: Run tests and verify failure**
- [x] **Step 3: Implement JDBC store and no-op fallback store**
- [x] **Step 4: Connect store into `DailyRadarJobService`**
- [x] **Step 5: Add DB id flow regression test and run full `mvn test`**

### Task 10: Analysis And Daily Report Persistence

**Files:**
- Create: `src/main/java/com/github/airadar/analysis/store/AnalysisStore.java`
- Create: `src/main/java/com/github/airadar/analysis/store/JdbcAnalysisStore.java`
- Create: `src/main/java/com/github/airadar/analysis/store/NoopAnalysisStore.java`
- Create: `src/main/java/com/github/airadar/report/store/DailyReportStore.java`
- Create: `src/main/java/com/github/airadar/report/store/JdbcDailyReportStore.java`
- Create: `src/main/java/com/github/airadar/report/store/NoopDailyReportStore.java`
- Modify: `src/main/java/com/github/airadar/analysis/model/RepositoryAnalysis.java`
- Modify: `src/main/java/com/github/airadar/report/model/DailyReport.java`
- Modify: `src/main/java/com/github/airadar/job/service/DailyRadarJobService.java`
- Test: `src/test/java/com/github/airadar/analysis/store/JdbcAnalysisStoreTest.java`
- Test: `src/test/java/com/github/airadar/report/store/JdbcDailyReportStoreTest.java`

**Interfaces:**
- Consumes: `RepositoryAnalysis` and `DailyReport`.
- Produces: `AnalysisStore.save` and `DailyReportStore.save`.

- [x] **Step 1: Write failing JDBC analysis/report persistence tests**
- [x] **Step 2: Run tests and verify failure**
- [x] **Step 3: Implement JDBC stores and no-op fallback stores**
- [x] **Step 4: Connect stores into `DailyRadarJobService`**
- [x] **Step 5: Run full `mvn test`**

### Task 11: OpenAI-Compatible AI Gateway Client

**Files:**
- Create: `src/main/java/com/github/airadar/analysis/gateway/AiGatewayProperties.java`
- Create: `src/main/java/com/github/airadar/analysis/gateway/OpenAiCompatibleAiGatewayClient.java`
- Modify: `src/main/resources/application.yml`
- Modify: `README.md`
- Test: `src/test/java/com/github/airadar/analysis/gateway/OpenAiCompatibleAiGatewayClientTest.java`

**Interfaces:**
- Consumes: `AiRequest`, `ai-gateway.base-url`, `ai-gateway.api-key`, and model role aliases.
- Produces: `AiGatewayClient.chat(AiRequest)` backed by OpenAI-compatible `/chat/completions`.

- [x] **Step 1: Write failing OpenAI-compatible client tests**
- [x] **Step 2: Run tests and verify failure**
- [x] **Step 3: Implement gateway properties and HTTP client**
- [x] **Step 4: Add model alias configuration and run full `mvn test`**

### Task 12: Prompt Templates And Structured Analysis Parsing

**Files:**
- Create: `src/main/java/com/github/airadar/analysis/prompt/AnalysisPrompt.java`
- Create: `src/main/java/com/github/airadar/analysis/prompt/AnalysisPromptService.java`
- Create: `src/main/java/com/github/airadar/analysis/service/RepositoryAnalysisParser.java`
- Modify: `src/main/java/com/github/airadar/job/service/DailyRadarJobService.java`
- Modify: `src/test/java/com/github/airadar/job/service/DailyRadarJobServiceTest.java`
- Modify: `src/test/java/com/github/airadar/config/SpringBeanWiringTest.java`
- Test: `src/test/java/com/github/airadar/analysis/prompt/AnalysisPromptServiceTest.java`
- Test: `src/test/java/com/github/airadar/analysis/service/RepositoryAnalysisParserTest.java`

**Interfaces:**
- Consumes: `RepositoryScore`, `AiResponse.content`.
- Produces: `AnalysisPromptService.lightPrompt`, `AnalysisPromptService.deepSourcePrompt`, and `RepositoryAnalysisParser.parse`.

- [x] **Step 1: Write failing prompt and parser tests**
- [x] **Step 2: Run tests and verify failure**
- [x] **Step 3: Implement prompt value object, prompt service, and JSON parser**
- [x] **Step 4: Connect prompt/parser into `DailyRadarJobService`**
- [x] **Step 5: Run full `mvn test`**

### Task 13: GitHub README, Commit, And Issue Context

**Files:**
- Create: `src/main/java/com/github/airadar/github/model/GithubRepositoryContext.java`
- Create: `src/main/java/com/github/airadar/github/dto/GithubReadmeResponse.java`
- Create: `src/main/java/com/github/airadar/github/dto/GithubCommitItem.java`
- Create: `src/main/java/com/github/airadar/github/dto/GithubIssueItem.java`
- Create: `src/main/java/com/github/airadar/github/service/GithubRepositoryContextCollector.java`
- Create: `src/main/java/com/github/airadar/github/service/GithubApiRepositoryContextCollector.java`
- Create: `src/main/java/com/github/airadar/github/service/NoopGithubRepositoryContextCollector.java`
- Modify: `src/main/java/com/github/airadar/analysis/prompt/AnalysisPromptService.java`
- Modify: `src/main/java/com/github/airadar/job/service/DailyRadarJobService.java`
- Modify: `src/test/java/com/github/airadar/job/service/DailyRadarJobServiceTest.java`
- Modify: `src/test/java/com/github/airadar/config/SpringBeanWiringTest.java`
- Test: `src/test/java/com/github/airadar/github/service/GithubApiRepositoryContextCollectorTest.java`

**Interfaces:**
- Consumes: `GithubRepository`.
- Produces: `GithubRepositoryContextCollector.collect(GithubRepository)` with README, recent commits, and recent issues.

- [x] **Step 1: Write failing context collector and prompt tests**
- [x] **Step 2: Run tests and verify failure**
- [x] **Step 3: Implement GitHub context DTOs and collector**
- [x] **Step 4: Connect context collector into Top 10 prompt generation**
- [x] **Step 5: Run full `mvn test`**

### Task 14: Top 3 Git Tree And Source Context

**Files:**
- Create: `src/main/java/com/github/airadar/github/dto/GithubTreeItem.java`
- Create: `src/main/java/com/github/airadar/github/dto/GithubTreeResponse.java`
- Create: `src/main/java/com/github/airadar/github/model/GithubSourceFile.java`
- Create: `src/main/java/com/github/airadar/github/model/GithubRepositorySourceContext.java`
- Create: `src/main/java/com/github/airadar/github/service/GithubRepositorySourceCollector.java`
- Create: `src/main/java/com/github/airadar/github/service/GithubApiRepositorySourceCollector.java`
- Create: `src/main/java/com/github/airadar/github/service/GithubSourceFileSelector.java`
- Create: `src/main/java/com/github/airadar/github/service/NoopGithubRepositorySourceCollector.java`
- Modify: `src/main/java/com/github/airadar/analysis/prompt/AnalysisPromptService.java`
- Modify: `src/main/java/com/github/airadar/job/service/DailyRadarJobService.java`
- Modify: `src/test/java/com/github/airadar/job/service/DailyRadarJobServiceTest.java`
- Modify: `src/test/java/com/github/airadar/config/SpringBeanWiringTest.java`
- Test: `src/test/java/com/github/airadar/github/service/GithubSourceFileSelectorTest.java`
- Test: `src/test/java/com/github/airadar/github/service/GithubApiRepositorySourceCollectorTest.java`

**Interfaces:**
- Consumes: `GithubRepository`.
- Produces: `GithubRepositorySourceCollector.collect(GithubRepository)` with selected source files for Top 3 coder-model analysis.

- [x] **Step 1: Write failing source selector, source collector, and job prompt tests**
- [x] **Step 2: Run tests and verify failure**
- [x] **Step 3: Implement Git tree DTOs, source context models, selector, and GitHub source collector**
- [x] **Step 4: Connect source context into Top 3 coder prompt generation**
- [x] **Step 5: Run full `mvn test`**
