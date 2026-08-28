package com.github.airadar.report.store;

import com.github.airadar.report.model.DailyReport;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcDailyReportStore implements DailyReportStore {

    private final NamedParameterJdbcOperations jdbc;

    public JdbcDailyReportStore(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(DailyReport report) {
        String sql = "INSERT INTO daily_report (report_date, title, markdown_content, "
                + "top_repository_ids, trend_summary) "
                + "VALUES (:reportDate, :title, :markdownContent, CAST(:topRepositoryIds AS jsonb), "
                + ":trendSummary) "
                + "ON CONFLICT (report_date) DO UPDATE SET "
                + "title = EXCLUDED.title, markdown_content = EXCLUDED.markdown_content, "
                + "top_repository_ids = EXCLUDED.top_repository_ids, "
                + "trend_summary = EXCLUDED.trend_summary, created_at = NOW()";
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("reportDate", report.getReportDate())
                .addValue("title", report.getTitle())
                .addValue("markdownContent", report.getMarkdownContent())
                .addValue("topRepositoryIds", toJsonArray(report.getTopRepositoryIds()))
                .addValue("trendSummary", report.getTrendSummary()));
    }

    private String toJsonArray(List<Long> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append(values.get(i));
        }
        json.append("]");
        return json.toString();
    }
}
