package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.FileUtil;
import com.ml.oilpricechecker.models.Price;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FileWriterService {

    public static final int MILLIS = 2000;

    @Async
    public void writePricesToFile(final List<Price> prices) {
        try {
            String date = getDateAsString();

            for (Price price: prices) {
                if (price.getSupplierName().toLowerCase().contains("craig")) {
                    FileUtil.writeToFile("craigs.txt", date, price.getPrice().substring(1));
                    FileUtil.writeToFile("weekly_comparison_craigs.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("scott")) {
                    FileUtil.writeToFile("scotts.txt", date, price.getPrice().substring(1));
                    FileUtil.writeToFile("weekly_comparison_scotts.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("campsie")) {
                    FileUtil.writeToFile("campsie.txt", date, price.getPrice().substring(1));
                    FileUtil.writeToFile("weekly_comparison_campsie.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("mcginley")) {
                    FileUtil.writeToFile("weekly_comparison_mcginleys.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("moore")) {
                    FileUtil.writeToFile("weekly_comparison_moores.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("nicholl")) {
                    FileUtil.writeToFile("weekly_comparison_nichollOils.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("springtown")) {
                    FileUtil.writeToFile("weekly_comparison_springtown.txt", date, price.getPrice().substring(1));
                }
            }
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    public void writeSixMonthDataToFile(final List<Price> prices) {
        try {
            String date = getDateAsString();

            for (Price price: prices) {
                if (price.getSupplierName().toLowerCase().contains("craig")) {
                    FileUtil.writeToFile("six_months_craigs.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("scott")) {
                    FileUtil.writeToFile("scotts.txt", date, price.getPrice().substring(1));
                    FileUtil.writeToFile("six_months_scotts.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("campsie")) {
                    FileUtil.writeToFile("campsie.txt", date, price.getPrice().substring(1));
                    FileUtil.writeToFile("six_months_campsie.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("mcginley")) {
                    FileUtil.writeToFile("six_months_mcginleys.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("moore")) {
                    FileUtil.writeToFile("six_months_moores.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("nicholl")) {
                    FileUtil.writeToFile("six_months_nichollOils.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("springtown")) {
                    FileUtil.writeToFile("six_months_springtown.txt", date, price.getPrice().substring(1));
                }
            }
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }



    private String getDateAsString() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);
        return formattedDate;
    }
}
