package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.SupplierPriceData;
import com.ml.oilpricechecker.file.FileUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ChartService {

    public List<SupplierPriceData> getChartData() {

        SupplierPriceData craigs = FileUtil.readDataFromFile("craigs.txt", "Craig Fuels");
        SupplierPriceData scotts = FileUtil.readDataFromFile("scotts.txt", "Scotts Fuels");
        SupplierPriceData campsie = FileUtil.readDataFromFile("campsie.txt", "Campsie Fuels");

        return Arrays.asList(craigs, scotts, campsie);
    }
}
