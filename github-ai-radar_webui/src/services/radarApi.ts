import { mockDailyReport } from '../mockData';
import type { DailyRadarReport } from '../types';

const API_BASE = import.meta.env.VITE_API_BASE_URL || '';

export async function fetchLatestReport(): Promise<DailyRadarReport> {
  try {
    const response = await fetch(`${API_BASE}/api/reports/latest`);
    if (!response.ok) {
      return mockDailyReport;
    }
    return await response.json() as DailyRadarReport;
  } catch {
    return mockDailyReport;
  }
}
