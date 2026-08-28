package com.github.airadar.report.store;

import com.github.airadar.report.model.DailyReport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(DailyReportStore.class)
public class NoopDailyReportStore implements DailyReportStore {

    @Override
    public void save(DailyReport report) {
    }
}
