package com.github.airadar.report.store;

import com.github.airadar.report.model.DailyReport;

public interface DailyReportStore {

    void save(DailyReport report);
}
