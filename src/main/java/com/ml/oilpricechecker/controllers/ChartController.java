package com.ml.oilpricechecker.controllers;

import com.ml.oilpricechecker.file.SupplierPriceData;
import com.ml.oilpricechecker.models.WeeklyComparison;
import com.ml.oilpricechecker.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChartController {

    private final ChartService chartService;

    @Autowired
    public ChartController(final ChartService chartService) {
        this.chartService = chartService;
    }

     @GetMapping("/fourteen-day-comparison")
     public List<SupplierPriceData> getFourteenDayComparisonData() throws Exception {

         List<SupplierPriceData> chartData = chartService.getChartData();

         return chartData;
     }

    @GetMapping("/weekly-comparison")
    public List<WeeklyComparison> getWeeklyComparisonData() throws Exception {

        List<WeeklyComparison> weeklyComparisons = chartService.getWeeklyComparisonData();

        return weeklyComparisons;
    }

    @GetMapping("/six-month-comparison")
    public List<SupplierPriceData> getSixMonthComparisonData() throws Exception {

        List<SupplierPriceData> chartData = chartService.getSixMonthData();

        return chartData;
    }
}
