package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.IFileHandler;
import com.ml.oilpricechecker.models.Price;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.ml.oilpricechecker.constants.Constants.*;

@Service
public class FileWriterService {

    @Autowired
    private IFileHandler fileHandler;

    public static Map<String, String> fourteenDaysDataMap = Map.of(
            CAMPSIE_DISPLAY_NAME, FOURTEEN_DAY_CAMPSIE_FILE_NAME,
            CRAIGS_DISPLAY_NAME,  FOURTEEN_DAY_CRAIGS_FILE_NAME,
            SCOTTS_DISPLAY_NAME,  FOURTEEN_DAY_SCOTTS_FILE_NAME);

    public static Map<String, String> weeklyDataMap = Map.of(
            CAMPSIE_DISPLAY_NAME,    WEEKLY_CAMPSIE_FILE_NAME,
            CRAIGS_DISPLAY_NAME,     WEEKLY_CRAIGS_FILE_NAME,
            MCGINLEY_DISPLAY_NAME,   WEEKLY_MCGINLEY_FILE_NAME,
            MOORES_DISPLAY_NAME,     WEEKLY_MOORES_FILE_NAME,
            SCOTTS_DISPLAY_NAME,     WEEKLY_SCOTTS_FILE_NAME,
            NICHOLLS_DISPLAY_NAME,   WEEKLY_NICHOLLS_FILE_NAME,
            SPRINGTOWN_DISPLAY_NAME, WEEKLY_SPRINGTOWN_FILE_NAME);

    public static Map<String, String> sixMonthsDataMap = Map.of(
            CAMPSIE_DISPLAY_NAME,    SIX_MONTHS_CAMPSIE_FILE_NAME,
            CRAIGS_DISPLAY_NAME,     SIX_MONTHS_CRAIGS_FILE_NAME,
            MCGINLEY_DISPLAY_NAME,   SIX_MONTHS_MCGINLEY_FILE_NAME,
            MOORES_DISPLAY_NAME,     SIX_MONTHS_MOORES_FILE_NAME,
            SCOTTS_DISPLAY_NAME,     SIX_MONTHS_SCOTTS_FILE_NAME,
            NICHOLLS_DISPLAY_NAME,   SIX_MONTHS_NICHOLLS_FILE_NAME,
            SPRINGTOWN_DISPLAY_NAME, SIX_MONTHS_SPRINGTOWN_FILE_NAME);


    @Async
    public void writePricesToFile(final List<Price> prices) {
        try {
            String date = getDateAsString();
            boolean updateSixMonthFiles = isSixMonthUpdateNeeded();

            for (Price price: prices) {
                fileHandler.writeToFile(getWeeklyFilename(price), date, getPrice(price));

                if (price.getSupplierName().equals(CRAIGS_DISPLAY_NAME)
                        || price.getSupplierName().equals(CAMPSIE_DISPLAY_NAME)
                        || price.getSupplierName().equals(SCOTTS_DISPLAY_NAME)) {
                    fileHandler.writeToFile(getFourteenDaysFilename(price), date, getPrice(price));
                }

                if (updateSixMonthFiles) {
                    fileHandler.writeToFile(getSixMonthsFilename(price), date, getPrice(price));
                }
            }
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    @Async
    public void writeSixMonthDataToFile(final List<Price> prices) {
        try {
            String date = getDateAsString();

            for (Price price: prices) {
                fileHandler.writeToFile(getSixMonthsFilename(price), date, getPrice(price));
            }
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    private String getPrice(final Price price) {
        return price.getPrice().substring(1);
    }

    private String getFourteenDaysFilename(final Price price) {
        return fourteenDaysDataMap.get(price.getSupplierName());
    }

    private String getSixMonthsFilename(final Price price) {
        return sixMonthsDataMap.get(price.getSupplierName());
    }

    private String getWeeklyFilename(final Price price) {
        return weeklyDataMap.get(price.getSupplierName());
    }

    private String getDateAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().format(formatter);
    }

    private boolean isSixMonthUpdateNeeded() {
        return LocalDate.now().getDayOfWeek() == DayOfWeek.FRIDAY;
    }
}
