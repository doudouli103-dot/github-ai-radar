export type TrendDirection = 'up' | 'flat' | 'down';

export interface HotRepository {
  id: number;
  fullName: string;
  htmlUrl: string;
  category: string;
  description: string;
  language: string;
  hotScore: number;
  starGrowth: number;
  forkGrowth: number;
  commitActivity: number;
  issueActivity: number;
  aiRelevance: number;
  freshness: number;
  summary: string;
  growthReason: string;
  businessValue: string;
  learningValue: string;
}

export interface DeepAnalysis {
  repositoryId: number;
  architectureSummary: string;
  technicalInnovation: string;
  keyFiles: string[];
}

export interface DailyRadarReport {
  reportDate: string;
  title: string;
  trendSummary: string;
  markdownContent: string;
  topRepositories: HotRepository[];
  deepAnalyses: DeepAnalysis[];
}

export interface TrendBand {
  name: string;
  direction: TrendDirection;
  strength: number;
}

export interface RepositoryRow extends HotRepository {
  rank: number;
  name: string;
}

export interface DeepDiveCard {
  rank: number;
  repository: HotRepository;
  architectureSummary: string;
  technicalInnovation: string;
  keyFiles: string[];
}

export interface DashboardViewModel {
  reportDate: string;
  title: string;
  trendSummary: string;
  markdownContent: string;
  trends: TrendBand[];
  topTen: RepositoryRow[];
  deepDiveCards: DeepDiveCard[];
}
