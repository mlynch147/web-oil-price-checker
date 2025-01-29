package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.FileData;
import com.ml.oilpricechecker.file.IFileHandler;
import com.ml.oilpricechecker.file.PriceDataPoint;
import com.ml.oilpricechecker.file.SupplierPriceData;
import com.ml.oilpricechecker.file.LocalFileHandler;
import com.ml.oilpricechecker.models.WeeklyComparison;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.ml.oilpricechecker.constants.Constants.*;

@Service
public class ChartService {

    @Autowired
    IFileHandler fileHandler;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    public List<SupplierPriceData> getChartData() {

        List<SupplierPriceData> suppliersData = new ArrayList<>();

        for (Map.Entry<String, String> entry : fourteenDaysFileNameMap.entrySet()) {
            suppliersData.add(createChartData(entry.getKey(), entry.getValue()));
        }

        return suppliersData;
    }

    public List<WeeklyComparison> getWeeklyComparisonData() {

        List<WeeklyComparison> weeklyComparisons = new ArrayList<>();

        for (Map.Entry<String, String> entry : weeklyFileNameMap.entrySet()) {
            String fileName = entry.getKey();
            String displayName = entry.getValue();
            List<FileData> weeklyData = fileHandler.getCurrentFileContent(fileName);

            weeklyComparisons.add(new WeeklyComparison(
                    displayName,
                    weeklyData.get(weeklyData.size() - 1).getAmount(),
                    weeklyData.get(0).getAmount()));
        }
        return weeklyComparisons;
    }

    public List<SupplierPriceData> getSixMonthData() {

        List<SupplierPriceData> allSuppliersData = new ArrayList<>();

        for (Map.Entry<String, String> entry : sixMonthsFileNameMap.entrySet()) {
            allSuppliersData.add(createChartData(entry.getKey(), entry.getValue()));
        }

        return allSuppliersData;
    }

    private SupplierPriceData createChartData(final String filename, final String displayName) {

        List<FileData> fileData = fileHandler.getCurrentFileContent(filename);

        List<PriceDataPoint> dataList = new ArrayList<>();

        for (FileData data: fileData) {

            PriceDataPoint priceDataPoint = new PriceDataPoint(
                    LocalDate.parse(data.getDate(), DATE_FORMATTER),
                    Double.parseDouble(data.getAmount()));

            dataList.add(priceDataPoint);
        }
        return new SupplierPriceData(dataList, displayName);
    }
}
