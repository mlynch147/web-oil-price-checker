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

import static com.ml.oilpricechecker.constants.Constants.*;

@Service
public class FileWriterService {

    @Autowired
    private IFileHandler fileHandler;

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
        if (isSixMonthUpdateNeeded()) {
            try {
                String date = getDateAsString();

                for (Price price : prices) {
                    fileHandler.writeToFile(getSixMonthsFilename(price), date,
                            getPrice(price));
                }
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        } else {
            System.out.println("Weekly scheduled task was triggered on wrong day: "
                    + LocalDate.now().getDayOfWeek().toString());
        }
    }

    private String getPrice(final Price price) {
        return price.getPrice().substring(1);
    }

    private String getFourteenDaysFilename(final Price price) {
        return fourteenDaysDisplayNameMap.get(price.getSupplierName());
    }

    private String getSixMonthsFilename(final Price price) {
        return sixMonthsDisplayNameMap.get(price.getSupplierName());
    }

    private String getWeeklyFilename(final Price price) {
        return weeklyDisplayNameMap.get(price.getSupplierName());
    }

    private String getDateAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().format(formatter);
    }

    private boolean isSixMonthUpdateNeeded() {
        return LocalDate.now().getDayOfWeek() == DayOfWeek.FRIDAY;
    }
}
