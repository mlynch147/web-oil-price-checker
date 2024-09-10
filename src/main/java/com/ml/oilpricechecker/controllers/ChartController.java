package com.ml.oilpricechecker.controllers;

import com.ml.oilpricechecker.file.ChartData;
import com.ml.oilpricechecker.file.ChartDataWithName;
import com.ml.oilpricechecker.models.Price;
import com.ml.oilpricechecker.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
     public ChartDataWithName getChartData() throws Exception {

         ChartDataWithName chartData = chartService.getChartData();

         return chartData;
     }
}

