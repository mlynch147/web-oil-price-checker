package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.FileData;
import com.ml.oilpricechecker.file.PriceDataPoint;
import com.ml.oilpricechecker.file.SupplierPriceData;
import com.ml.oilpricechecker.file.FileUtil;
import com.ml.oilpricechecker.models.WeeklyComparison;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ChartService {

    public static Map<String, String> weeklyDataMap = Map.of(
            "weekly_comparison_campsie.txt", "Campsie Fuels",
            "weekly_comparison_craigs.txt", "Craig Fuels",
            "weekly_comparison_mcginleys.txt", "McGinley Oils",
            "weekly_comparison_moores.txt", "Moores Fuels",
            "weekly_comparison_nichollOils.txt", "Nicholl Oils",
            "weekly_comparison_scotts.txt", "Scotts Fuels",
            "weekly_comparison_springtown.txt", "Springtown Fuels"
    );

    public static Map<String, String> sixMonthsDataMap = Map.of(
            "six_months_campsie.txt", "Campsie Fuels",
            "six_months_craigs.txt", "Craig Fuels",
            "six_months_mcginleys.txt", "McGinley Oils",
            "six_months_moores.txt", "Moores Fuels",
            "six_months_nichollOils.txt", "Nicholl Oils",
            "six_months_scotts.txt", "Scotts Fuels",
            "six_months_springtown.txt", "Springtown Fuels"
    );


    public List<SupplierPriceData> getChartData() {

        SupplierPriceData craigs = createChartData("craigs.txt", "Craig Fuels");
        SupplierPriceData scotts = createChartData("scotts.txt", "Scotts Fuels");
        SupplierPriceData campsie = createChartData("campsie.txt", "Campsie Fuels");

        return Arrays.asList(craigs, scotts, campsie);
    }

    public List<WeeklyComparison> getWeeklyComparisonData() {

        List<WeeklyComparison> weeklyComparisons = new ArrayList<>();

        for (Map.Entry<String, String> entry : weeklyDataMap.entrySet()) {
            String fileName = entry.getKey();
            String displayName = entry.getValue();

            List<FileData> weeklyData = FileUtil.getCurrentFileContent(fileName);

            weeklyComparisons.add(new WeeklyComparison(
                    displayName,
                    weeklyData.get(weeklyData.size() - 1).getAmount(),
                    weeklyData.get(0).getAmount()));
        }

        return weeklyComparisons;
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

    public List<SupplierPriceData> getSixMonthData() {

        List<SupplierPriceData> allSuppliersData = new ArrayList<>();

        for (Map.Entry<String, String> entry : sixMonthsDataMap.entrySet()) {
            allSuppliersData.add(createChartData(entry.getKey(), entry.getValue()));
        }

        return allSuppliersData;
    }
}
