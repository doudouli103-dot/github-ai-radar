import type { DailyRadarReport } from './types';

export const mockDailyReport: DailyRadarReport = {
  reportDate: '2026-08-28',
  title: 'GitHub AI Daily - 2026-08-28',
  trendSummary: 'Agent up strongly; Coding Agent up; MCP up; RAG flat',
  markdownContent: `# GitHub AI Daily

今日趋势

Agent 和 Coding Agent 继续升温，MCP 生态项目增长明显，RAG 进入稳定迭代期。

今日最值得研究

owner/agent-runtime

原因：它把 Agent Loop、工具调用、上下文压缩和模型路由放进了一个可扩展运行时，适合作为后续工程拆解样本。`,
  topRepositories: [
    {
      id: 1,
      fullName: 'owner/agent-runtime',
      htmlUrl: 'https://github.com/owner/agent-runtime',
      category: 'Agent',
      description: 'Composable runtime for production AI agents.',
      language: 'Python',
      hotScore: 96.4,
      starGrowth: 1200,
      forkGrowth: 220,
      commitActivity: 42,
      issueActivity: 31,
      aiRelevance: 98,
      freshness: 88,
      summary: '面向生产环境的 Agent Runtime，重点在 loop、tools、memory 和 router。',
      growthReason: '开发者正在寻找可直接落地的 Agent 工程框架。',
      businessValue: '可沉淀为企业内部 Agent 编排平台底座。',
      learningValue: '适合学习 Agent Loop、Tool Calling 和 Context Management。'
    },
    {
      id: 2,
      fullName: 'owner/coding-agent-kit',
      htmlUrl: 'https://github.com/owner/coding-agent-kit',
      category: 'Coding',
      description: 'Toolkit for repository-aware coding agents.',
      language: 'TypeScript',
      hotScore: 91.2,
      starGrowth: 860,
      forkGrowth: 140,
      commitActivity: 36,
      issueActivity: 24,
      aiRelevance: 96,
      freshness: 84,
      summary: '围绕代码检索、补丁生成和测试反馈构建 Coding Agent 工作流。',
      growthReason: 'Claude Code 类产品带动开源替代方案热度。',
      businessValue: '可服务研发提效、代码审查和自动修复场景。',
      learningValue: '适合研究 repo map、patch apply 和测试闭环。'
    },
    {
      id: 3,
      fullName: 'owner/mcp-workbench',
      htmlUrl: 'https://github.com/owner/mcp-workbench',
      category: 'MCP',
      description: 'Workbench for building and testing MCP servers.',
      language: 'Go',
      hotScore: 88.7,
      starGrowth: 620,
      forkGrowth: 96,
      commitActivity: 27,
      issueActivity: 21,
      aiRelevance: 94,
      freshness: 81,
      summary: '提供 MCP Server 调试、工具 schema 检查和本地联调能力。',
      growthReason: 'MCP 工具生态扩张，开发者需要调试台。',
      businessValue: '可作为企业工具接入 AI 助手的开发入口。',
      learningValue: '适合学习 MCP 协议、工具声明和连接生命周期。'
    },
    {
      id: 4,
      fullName: 'owner/rag-eval-lab',
      htmlUrl: 'https://github.com/owner/rag-eval-lab',
      category: 'RAG',
      description: 'Evaluation lab for retrieval augmented generation.',
      language: 'Python',
      hotScore: 78.5,
      starGrowth: 240,
      forkGrowth: 48,
      commitActivity: 18,
      issueActivity: 12,
      aiRelevance: 91,
      freshness: 70,
      summary: '聚焦 RAG 评测、召回质量和答案可信度。',
      growthReason: 'RAG 从搭建进入评测和质量治理阶段。',
      businessValue: '可用于企业知识库质量评估。',
      learningValue: '适合学习 embedding、rerank 和 eval dataset。'
    }
  ],
  deepAnalyses: [
    {
      repositoryId: 1,
      architectureSummary: '核心由 planner、agent loop、tool registry、memory store 和 model router 组成。',
      technicalInnovation: '把工具调用结果压缩成结构化 memory event，再交给 router 选择下一轮模型。',
      keyFiles: ['src/agent/loop.py', 'src/tools/registry.py', 'src/memory/store.py']
    },
    {
      repositoryId: 2,
      architectureSummary: '通过 repo index、task planner、patch writer 和 test runner 构成闭环。',
      technicalInnovation: '把测试失败作为下一轮上下文输入，形成自动修复反馈链。',
      keyFiles: ['src/coder/router.ts', 'src/patch/applyPatch.ts', 'src/tests/runner.ts']
    },
    {
      repositoryId: 3,
      architectureSummary: '由 server registry、schema validator 和 request inspector 组成。',
      technicalInnovation: '在本地模拟 Host 与 MCP Server 的交互并记录工具调用轨迹。',
      keyFiles: ['internal/mcp/server.go', 'internal/schema/validator.go', 'cmd/workbench/main.go']
    }
  ]
};
