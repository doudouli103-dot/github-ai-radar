import type { DailyRadarReport, DashboardViewModel, DeepDiveCard, RepositoryRow, TrendBand } from './types';

export function buildDashboardViewModel(report: DailyRadarReport): DashboardViewModel {
  const topTen = report.topRepositories
    .slice()
    .sort((left, right) => right.hotScore - left.hotScore)
    .slice(0, 10)
    .map<RepositoryRow>((repository, index) => ({
      ...repository,
      rank: index + 1,
      name: repository.fullName
    }));

  const deepDiveCards = report.deepAnalyses
    .slice(0, 3)
    .map<DeepDiveCard>((analysis, index) => {
      const repository = report.topRepositories.find((item) => item.id === analysis.repositoryId);
      if (!repository) {
        throw new Error(`Missing repository for deep analysis: ${analysis.repositoryId}`);
      }
      return {
        rank: index + 1,
        repository,
        architectureSummary: analysis.architectureSummary,
        technicalInnovation: analysis.technicalInnovation,
        keyFiles: analysis.keyFiles
      };
    });

  return {
    reportDate: report.reportDate,
    title: report.title,
    trendSummary: report.trendSummary,
    markdownContent: report.markdownContent,
    trends: parseTrends(report.trendSummary),
    topTen,
    deepDiveCards
  };
}

function parseTrends(summary: string): TrendBand[] {
  return summary
    .split(';')
    .map((part) => part.trim())
    .filter(Boolean)
    .map((part) => {
      const lower = part.toLowerCase();
      const name = part.replace(/\s+(up strongly|up|flat|down).*$/i, '').trim();
      if (lower.includes('up strongly')) {
        return { name, direction: 'up', strength: 2 };
      }
      if (lower.includes('up')) {
        return { name, direction: 'up', strength: 1 };
      }
      if (lower.includes('down')) {
        return { name, direction: 'down', strength: -1 };
      }
      return { name, direction: 'flat', strength: 0 };
    });
}
