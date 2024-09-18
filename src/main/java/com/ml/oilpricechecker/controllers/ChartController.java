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

    public static final int DEFAULT_NUMBER_OF_LITRES = 500;
    private final ChartService chartService;

    @Autowired
    public ChartController(final ChartService chartService) {
        this.chartService = chartService;
    }

     @GetMapping("/chart-data")
     public List<SupplierPriceData> getChartData() throws Exception {

         List<SupplierPriceData> chartData = chartService.getChartData();

         return chartData;
     }



    @GetMapping("/weekly-comparison")
    public List<WeeklyComparison> getWeeklyComparisonData() throws Exception {

        List<WeeklyComparison> weeklyComparisons = chartService.getWeeklyComparisonData();

        return weeklyComparisons;
    }
}

