# Local Device Topology

GitHub AI Radar uses a three-device local architecture.

## Device Roles

| Device | Configuration | Recommended Role |
| --- | --- | --- |
| MacBook Pro | 24G / 1T | Development terminal, control console, debugging entry |
| Mac Studio | 128G / 1T | Model capability layer: LLM, Embedding, Reranker |
| Windows | 16G / 1T SSD / NVIDIA 4G | Data service layer: MySQL, Redis, Elasticsearch, Chroma, RAG API |

## Runtime Flow

```text
MacBook Pro
  |
  | runs IDE, Codex, Maven, local debugging
  v
GitHub AI Radar Java App
  |
  +--> GitHub REST API
  |
  +--> Mac Studio Model Gateway
  |      - LLM
  |      - Embedding
  |      - Reranker
  |
  +--> Windows Data Service Layer
         - MySQL / PostgreSQL-compatible storage as selected
         - Redis
         - Elasticsearch
         - Chroma
         - RAG API
```

## Boundary Decisions

The Java application remains the orchestration backbone.

Mac Studio exposes model capabilities behind stable HTTP endpoints. The Java application should call `ai-gateway.base-url` or `infrastructure.mac-studio.model-gateway-url`, not bind directly to a concrete local model.

Windows provides data and retrieval services. MVP persistence currently uses PostgreSQL-compatible schema files. If Windows is standardized on MySQL for this project, add a separate MySQL migration path instead of silently reusing PostgreSQL DDL.

## Environment Variables

```bash
export MAC_STUDIO_MODEL_GATEWAY_URL=http://mac-studio.local:4000/v1
export MAC_STUDIO_EMBEDDING_URL=http://mac-studio.local:4000/v1/embeddings
export MAC_STUDIO_RERANKER_URL=http://mac-studio.local:4000/v1/rerank

export WINDOWS_MYSQL_URL=jdbc:mysql://windows.local:3306/github_ai_radar
export WINDOWS_REDIS_URL=redis://windows.local:6379
export WINDOWS_ELASTICSEARCH_URL=http://windows.local:9200
export WINDOWS_CHROMA_URL=http://windows.local:8001
export WINDOWS_RAG_API_URL=http://windows.local:8000
```

## MVP Rule

Do not move orchestration into Python services in the first version. Python services may exist behind the Windows RAG API or Mac Studio model tools, but Spring Boot remains the daily job owner.
