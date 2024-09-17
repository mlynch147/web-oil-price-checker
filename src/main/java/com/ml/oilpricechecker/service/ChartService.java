package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.FileData;
import com.ml.oilpricechecker.file.PriceDataPoint;
import com.ml.oilpricechecker.file.SupplierPriceData;
import com.ml.oilpricechecker.file.FileUtil;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class ChartService {

    public List<SupplierPriceData> getChartData() {

        SupplierPriceData craigs = createChartData("craigs.txt", "Craig Fuels");
        SupplierPriceData scotts = createChartData("scotts.txt", "Scotts Fuels");
        SupplierPriceData campsie = createChartData("campsie.txt", "Campsie Fuels");

        return Arrays.asList(craigs, scotts, campsie);
    }


    private SupplierPriceData createChartData(final String filename, final String displayName) {

        List<FileData> fileData = FileUtil.getCurrentFileContent(filename);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        List<PriceDataPoint> dataList = new ArrayList<>();

        for (FileData data: fileData) {

            PriceDataPoint priceDataPoint = null;
            try {
                priceDataPoint = new PriceDataPoint(
                        dateFormat.parse(data.getDate()), Double.parseDouble(data.getAmount()));

                dataList.add(priceDataPoint);

            } catch (ParseException e) {
               e.printStackTrace();
            }
        }
        return new SupplierPriceData(dataList, displayName);
    }
}
