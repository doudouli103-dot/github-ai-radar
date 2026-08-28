<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { Activity, BookOpen, CalendarDays, Code2, ExternalLink, FileText, Gauge, RefreshCw, Search } from '@lucide/vue';
import { fetchLatestReport } from './services/radarApi';
import { buildDashboardViewModel } from './viewModel';
import type { DailyRadarReport } from './types';

const report = ref<DailyRadarReport | null>(null);
const activeTab = ref<'today' | 'history'>('today');
const selectedRepositoryId = ref<number | null>(null);
const loading = ref(true);

const dashboard = computed(() => report.value ? buildDashboardViewModel(report.value) : null);
const selectedRepository = computed(() => {
  if (!dashboard.value) {
    return null;
  }
  return dashboard.value.topTen.find((repository) => repository.id === selectedRepositoryId.value)
    || dashboard.value.topTen[0]
    || null;
});

async function loadReport() {
  loading.value = true;
  report.value = await fetchLatestReport();
  selectedRepositoryId.value = report.value.topRepositories[0]?.id ?? null;
  loading.value = false;
}

onMounted(loadReport);
</script>

<template>
  <main class="app-shell">
    <aside class="sidebar">
      <div class="brand-block">
        <div class="brand-mark">
          <Activity :size="22" />
        </div>
        <div>
          <h1>GitHub AI Radar</h1>
          <p>Open Source Intelligence</p>
        </div>
      </div>

      <nav class="nav-list" aria-label="Console navigation">
        <button :class="{ active: activeTab === 'today' }" type="button" @click="activeTab = 'today'">
          <Gauge :size="18" />
          <span>今日雷达</span>
        </button>
        <button :class="{ active: activeTab === 'history' }" type="button" @click="activeTab = 'history'">
          <CalendarDays :size="18" />
          <span>历史日报</span>
        </button>
      </nav>

      <section v-if="dashboard" class="date-panel">
        <span>当前报告</span>
        <strong>{{ dashboard.reportDate }}</strong>
      </section>
    </aside>

    <section class="workspace">
      <header class="topbar">
        <div>
          <p class="eyebrow">Daily AI Open Source Radar</p>
          <h2>{{ dashboard?.title || 'GitHub AI Daily' }}</h2>
        </div>
        <button class="icon-button" type="button" title="刷新日报" @click="loadReport">
          <RefreshCw :size="18" />
        </button>
      </header>

      <div v-if="loading" class="loading-state">Loading radar report...</div>

      <template v-else-if="dashboard && activeTab === 'today'">
        <section class="trend-strip" aria-label="Trend summary">
          <article v-for="trend in dashboard.trends" :key="trend.name" class="trend-tile">
            <span>{{ trend.name }}</span>
            <strong :class="`trend-${trend.direction}`">
              {{ trend.direction === 'up' ? (trend.strength > 1 ? '↑↑' : '↑') : trend.direction === 'down' ? '↓' : '→' }}
            </strong>
          </article>
        </section>

        <section class="summary-band">
          <div>
            <p class="section-kicker">今日趋势</p>
            <h3>{{ dashboard.trendSummary }}</h3>
          </div>
          <div class="metric-cluster">
            <div>
              <span>Top 项目</span>
              <strong>{{ dashboard.topTen.length }}</strong>
            </div>
            <div>
              <span>深度分析</span>
              <strong>{{ dashboard.deepDiveCards.length }}</strong>
            </div>
          </div>
        </section>

        <section class="content-grid">
          <div class="table-panel">
            <div class="section-heading">
              <div>
                <p class="section-kicker">Top 10</p>
                <h3>快速升温项目</h3>
              </div>
              <Search :size="19" />
            </div>
            <div class="repo-table">
              <button
                v-for="repository in dashboard.topTen"
                :key="repository.id"
                :class="{ selected: selectedRepository?.id === repository.id }"
                type="button"
                class="repo-row"
                @click="selectedRepositoryId = repository.id"
              >
                <span class="rank">#{{ repository.rank }}</span>
                <span class="repo-main">
                  <strong>{{ repository.name }}</strong>
                  <small>{{ repository.description }}</small>
                </span>
                <span class="category">{{ repository.category }}</span>
                <span class="score">{{ repository.hotScore.toFixed(1) }}</span>
                <span class="growth">+{{ repository.starGrowth }}</span>
              </button>
            </div>
          </div>

          <aside v-if="selectedRepository" class="detail-panel">
            <div class="section-heading">
              <div>
                <p class="section-kicker">项目定位</p>
                <h3>{{ selectedRepository.fullName }}</h3>
              </div>
              <a :href="selectedRepository.htmlUrl" target="_blank" rel="noreferrer" title="打开 GitHub">
                <ExternalLink :size="18" />
              </a>
            </div>
            <dl class="detail-list">
              <div>
                <dt>为什么火</dt>
                <dd>{{ selectedRepository.growthReason }}</dd>
              </div>
              <div>
                <dt>商业价值</dt>
                <dd>{{ selectedRepository.businessValue }}</dd>
              </div>
              <div>
                <dt>学习价值</dt>
                <dd>{{ selectedRepository.learningValue }}</dd>
              </div>
            </dl>
          </aside>
        </section>

        <section class="deep-section">
          <div class="section-heading">
            <div>
              <p class="section-kicker">Top 3</p>
              <h3>源码深度分析</h3>
            </div>
            <Code2 :size="20" />
          </div>
          <div class="deep-grid">
            <article v-for="card in dashboard.deepDiveCards" :key="card.repository.id" class="deep-card">
              <div class="card-title-row">
                <span class="rank">#{{ card.rank }}</span>
                <h4>{{ card.repository.fullName }}</h4>
              </div>
              <p>{{ card.architectureSummary }}</p>
              <p>{{ card.technicalInnovation }}</p>
              <div class="file-list">
                <span v-for="file in card.keyFiles" :key="file">
                  <BookOpen :size="14" />
                  {{ file }}
                </span>
              </div>
            </article>
          </div>
        </section>
      </template>

      <template v-else-if="dashboard && activeTab === 'history'">
        <section class="history-layout">
          <div class="section-heading">
            <div>
              <p class="section-kicker">Markdown</p>
              <h3>历史日报预览</h3>
            </div>
            <FileText :size="20" />
          </div>
          <pre class="markdown-preview">{{ dashboard.markdownContent }}</pre>
        </section>
      </template>
    </section>
  </main>
</template>
