package com.github.airadar.report.store;

import com.github.airadar.report.model.DailyReport;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JdbcDailyReportStoreTest {

    @Test
    void upsertsDailyReportByReportDate() {
        NamedParameterJdbcOperations jdbc = mock(NamedParameterJdbcOperations.class);
        JdbcDailyReportStore store = new JdbcDailyReportStore(jdbc);

        store.save(new DailyReport(LocalDate.of(2026, 8, 27),
                "GitHub AI Daily - 2026-08-27", "# Report",
                Arrays.asList(1L, 2L, 3L), "Agent projects are heating up"));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbc).update(sqlCaptor.capture(), paramsCaptor.capture());

        assertThat(sqlCaptor.getValue()).contains("INSERT INTO daily_report");
        assertThat(sqlCaptor.getValue()).contains("ON CONFLICT (report_date)");
        assertThat(paramsCaptor.getValue().getValue("reportDate")).isEqualTo(LocalDate.of(2026, 8, 27));
        assertThat(paramsCaptor.getValue().getValue("topRepositoryIds").toString()).contains("1");
        assertThat(paramsCaptor.getValue().getValue("trendSummary")).isEqualTo("Agent projects are heating up");
    }
}
