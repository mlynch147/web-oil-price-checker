package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.FileData;
import com.ml.oilpricechecker.file.PriceDataPoint;
import com.ml.oilpricechecker.file.SupplierPriceData;
import com.ml.oilpricechecker.file.FileUtil;
import com.ml.oilpricechecker.models.WeeklyComparison;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.ml.oilpricechecker.constants.Constants.*;

@Service
public class ChartService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    public static Map<String, String> weeklyDataMap = Map.of(
            WEEKLY_CAMPSIE_FILE_NAME, CAMPSIE_DISPLAY_NAME,
            WEEKLY_CRAIGS_FILE_NAME, CRAIGS_DISPLAY_NAME,
            WEEKLY_MCGINLEY_FILE_NAME, MCGINLEY_DISPLAY_NAME,
            WEEKLY_MOORES_FILE_NAME, MOORES_DISPLAY_NAME,
            WEEKLY_SCOTTS_FILE_NAME, SCOTTS_DISPLAY_NAME,
            WEEKLY_NICHOLLS_FILE_NAME, NICHOLLS_DISPLAY_NAME,
            WEEKLY_SPRINGTOWN_FILE_NAME, SPRINGTOWN_DISPLAY_NAME
    );

    public static Map<String, String> sixMonthsDataMap = Map.of(
            SIX_MONTHS_CAMPSIE_FILE_NAME, CAMPSIE_DISPLAY_NAME,
            SIX_MONTHS_CRAIGS_FILE_NAME, CRAIGS_DISPLAY_NAME,
            SIX_MONTHS_MCGINLEY_FILE_NAME, MCGINLEY_DISPLAY_NAME,
            SIX_MONTHS_MOORES_FILE_NAME, MOORES_DISPLAY_NAME,
            SIX_MONTHS_SCOTTS_FILE_NAME, SCOTTS_DISPLAY_NAME,
            SIX_MONTHS_NICHOLLS_FILE_NAME, NICHOLLS_DISPLAY_NAME,
            SIX_MONTHS_SPRINGTOWN_FILE_NAME, SPRINGTOWN_DISPLAY_NAME
    );


    public List<SupplierPriceData> getChartData() {

        SupplierPriceData craigs = createChartData(FOURTEEN_DAY_CRAIGS_FILE_NAME, CRAIGS_DISPLAY_NAME);
        SupplierPriceData scotts = createChartData(FOURTEEN_DAY_SCOTTS_FILE_NAME, SCOTTS_DISPLAY_NAME);
        SupplierPriceData campsie = createChartData(FOURTEEN_DAY_CAMPSIE_FILE_NAME, CAMPSIE_DISPLAY_NAME);

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

        List<PriceDataPoint> dataList = new ArrayList<>();

        for (FileData data: fileData) {

            PriceDataPoint priceDataPoint = new PriceDataPoint(
                    LocalDate.parse(data.getDate(), DATE_FORMATTER),
                    Double.parseDouble(data.getAmount()));

            dataList.add(priceDataPoint);
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
