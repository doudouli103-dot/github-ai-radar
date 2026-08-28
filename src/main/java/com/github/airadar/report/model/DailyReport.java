package com.github.airadar.report.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class DailyReport {

    private final LocalDate reportDate;
    private final String title;
    private final String markdownContent;
    private final List<Long> topRepositoryIds;
    private final String trendSummary;

    public DailyReport(LocalDate reportDate, String title, String markdownContent) {
        this(reportDate, title, markdownContent, Collections.<Long>emptyList(), null);
    }

    public DailyReport(LocalDate reportDate, String title, String markdownContent,
                       List<Long> topRepositoryIds, String trendSummary) {
        this.reportDate = reportDate;
        this.title = title;
        this.markdownContent = markdownContent;
        this.topRepositoryIds = topRepositoryIds == null ? Collections.<Long>emptyList() : topRepositoryIds;
        this.trendSummary = trendSummary;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public String getTitle() {
        return title;
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public List<Long> getTopRepositoryIds() {
        return topRepositoryIds;
    }

    public String getTrendSummary() {
        return trendSummary;
    }
}
