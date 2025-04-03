package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.FileData;
import com.ml.oilpricechecker.file.IFileHandler;
import com.ml.oilpricechecker.file.PriceDataPoint;
import com.ml.oilpricechecker.file.SupplierPriceData;
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
            suppliersData.add(createChartData(entry.getKey(), entry.getValue(), true));
        }

        return suppliersData;
    }

    public List<WeeklyComparison> getWeeklyComparisonData() {

        List<WeeklyComparison> weeklyComparisons = new ArrayList<>();

        for (Map.Entry<String, String> entry : weeklyFileNameMap.entrySet()) {
            String fileName = entry.getKey();
            String displayName = entry.getValue();
            List<FileData> weeklyData = fileHandler.getCurrentFileContent(fileName);

            if (hasGapsInData(weeklyData)) {
                fillGapsInData(weeklyData);

                if (weeklyData.size() > IFileHandler.MAX_WEEKLY_COMPARISON_DAYS) {
                    weeklyData = weeklyData.subList(
                            weeklyData.size() - IFileHandler.MAX_WEEKLY_COMPARISON_DAYS, weeklyData.size());

                    fileHandler.rewriteFile(fileName, weeklyData);
                }
            }


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
            allSuppliersData.add(createChartData(entry.getKey(), entry.getValue(), false));
        }

        return allSuppliersData;
    }

    private SupplierPriceData createChartData(final String filename, final String displayName, final boolean fillGaps) {

        List<FileData> fileData = fileHandler.getCurrentFileContent(filename);

        if (fillGaps && hasGapsInData(fileData)) {
            fillGapsInData(fileData);

            if (fileData.size() > IFileHandler.MAX_CHART_DATA_DAYS) {
                fileData = fileData.subList(
                        fileData.size() - IFileHandler.MAX_CHART_DATA_DAYS,
                        fileData.size());

                fileHandler.rewriteFile(filename, fileData);
            }
        }

        List<PriceDataPoint> dataList = new ArrayList<>();

        for (FileData data: fileData) {

            PriceDataPoint priceDataPoint = new PriceDataPoint(
                    LocalDate.parse(data.getDate(), DATE_FORMATTER),
                    Double.parseDouble(data.getAmount()));

            dataList.add(priceDataPoint);
        }
        return new SupplierPriceData(dataList, displayName);
    }

    private void fillGapsInData(final List<FileData> fileDataList) {
        int i = 0;
        while (i < fileDataList.size() - 1) {
            FileData current = fileDataList.get(i);
            FileData next = fileDataList.get(i + 1);

            LocalDate currentDate = LocalDate.parse(current.getDate(), DATE_FORMATTER);
            LocalDate nextDate = LocalDate.parse(next.getDate(), DATE_FORMATTER);

            LocalDate expectedNextDate = currentDate.plusDays(1);
            while (expectedNextDate.isBefore(nextDate)) {
                fileDataList.add(i + 1, new FileData(expectedNextDate.format(DATE_FORMATTER), current.getAmount()));
                expectedNextDate = expectedNextDate.plusDays(1);
                i++; // Move forward for each inserted element
            }
            i++; // Move to the next original element
        }
    }



    private boolean hasGapsInData(final List<FileData> dataList) {

        for (int i = 0; i < dataList.size() - 1; i++) {
            LocalDate current = LocalDate.parse(dataList.get(i).getDate(), DATE_FORMATTER);
            LocalDate next = LocalDate.parse(dataList.get(i + 1).getDate(), DATE_FORMATTER);

            current = current.plusDays(1);
            if (current.getDayOfMonth() != next.getDayOfMonth()) {
                return true;
            }
        }

        return false;
    }

}
