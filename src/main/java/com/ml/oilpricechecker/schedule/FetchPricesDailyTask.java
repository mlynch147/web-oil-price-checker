package com.ml.oilpricechecker.schedule;

import com.ml.oilpricechecker.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FetchPricesDailyTask {

    private final ChartService chartService;

    // Constructor injection of the MyService dependency
    @Autowired
    public FetchPricesDailyTask(final ChartService chartService) {
        this.chartService = chartService;
    }

    // This method will run at 23:55  every day
    @Scheduled(cron = "0 55 23 * * ?")
    public void executeTask() {
        System.out.println("Running scheduled task at 2 AM");
        // Your task logic here
    }
}
