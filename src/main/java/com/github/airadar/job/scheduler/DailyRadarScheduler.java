package com.github.airadar.job.scheduler;

import com.github.airadar.job.service.DailyRadarJobService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyRadarScheduler {

    private final DailyRadarJobService dailyRadarJobService;

    public DailyRadarScheduler(DailyRadarJobService dailyRadarJobService) {
        this.dailyRadarJobService = dailyRadarJobService;
    }

    @Scheduled(cron = "${radar.scheduler.daily-cron}")
    public void runDaily() {
        dailyRadarJobService.run(LocalDate.now());
    }
}
