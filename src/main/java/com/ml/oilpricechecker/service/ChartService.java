package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.ChartData;
import com.ml.oilpricechecker.file.ChartDataWithName;
import com.ml.oilpricechecker.file.FileUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ChartService {

    public List<ChartDataWithName> getChartData() {

        ChartDataWithName craigs = FileUtil.readDataFromFile("craigs.txt", "Craig Fuels");
        ChartDataWithName scotts = FileUtil.readDataFromFile("scotts.txt", "Scotts Fuels");
        ChartDataWithName campsie = FileUtil.readDataFromFile("campsie.txt", "Campsie Fuels");

        return Arrays.asList(craigs, scotts, campsie);
    }
}
