import { describe, expect, it } from 'vitest';
import { buildDashboardViewModel } from '../viewModel';
import type { DailyRadarReport } from '../types';

describe('buildDashboardViewModel', () => {
  it('derives top repositories, deep analyses, and trend bands for the console', () => {
    const report: DailyRadarReport = {
      reportDate: '2026-08-28',
      title: 'GitHub AI Daily - 2026-08-28',
      trendSummary: 'Agent up strongly; Coding Agent up; RAG flat',
      markdownContent: '# GitHub AI Daily',
      topRepositories: [
        repository(1, 'owner/agent-one', 'Agent', 96.4, 1200),
        repository(2, 'owner/coder-two', 'Coding', 91.2, 880),
        repository(3, 'owner/rag-three', 'RAG', 88.1, 430),
        repository(4, 'owner/mcp-four', 'MCP', 77.0, 120)
      ],
      deepAnalyses: [
        deep(1, 'src/agent/loop.py'),
        deep(2, 'src/coder/router.ts'),
        deep(3, 'src/rag/retriever.py')
      ]
    };

    const viewModel = buildDashboardViewModel(report);

    expect(viewModel.topTen).toHaveLength(4);
    expect(viewModel.topTen[0].rank).toBe(1);
    expect(viewModel.topTen[0].name).toBe('owner/agent-one');
    expect(viewModel.deepDiveCards.map((card) => card.keyFiles[0])).toEqual([
      'src/agent/loop.py',
      'src/coder/router.ts',
      'src/rag/retriever.py'
    ]);
    expect(viewModel.trends).toEqual([
      { name: 'Agent', direction: 'up', strength: 2 },
      { name: 'Coding Agent', direction: 'up', strength: 1 },
      { name: 'RAG', direction: 'flat', strength: 0 }
    ]);
  });
});

function repository(
  id: number,
  fullName: string,
  category: string,
  hotScore: number,
  starGrowth: number
) {
  return {
    id,
    fullName,
    htmlUrl: `https://github.com/${fullName}`,
    category,
    description: `${fullName} description`,
    language: 'TypeScript',
    hotScore,
    starGrowth,
    forkGrowth: 80,
    commitActivity: 24,
    issueActivity: 16,
    aiRelevance: 95,
    freshness: 82,
    summary: `${fullName} summary`,
    growthReason: `${fullName} growth`,
    businessValue: `${fullName} business value`,
    learningValue: `${fullName} learning value`
  };
}

function deep(repositoryId: number, file: string) {
  return {
    repositoryId,
    architectureSummary: `Architecture for ${repositoryId}`,
    technicalInnovation: `Innovation for ${repositoryId}`,
    keyFiles: [file]
  };
}
