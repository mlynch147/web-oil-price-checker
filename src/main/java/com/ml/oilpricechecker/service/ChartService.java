package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.ChartData;
import com.ml.oilpricechecker.file.ChartDataWithName;
import com.ml.oilpricechecker.file.FileUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChartService {

    public ChartDataWithName getChartData() {

        ChartDataWithName data = FileUtil.readDataFromFile("craigs.txt", "Craig Fuels");

        return data;
    }
}
