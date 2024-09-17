package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.file.FileUtil;
import com.ml.oilpricechecker.models.Price;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FileWriterService {

    public static final int MILLIS = 2000;

    @Async  // This makes the method asynchronous
    public void writePricesToFile(final List<Price> prices) {
        // Logic to write the list of prices to a file
        // Example: Write to a CSV, JSON, etc.
        // Make sure this runs asynchronously.
        // For instance, using a BufferedWriter or Files API
        try {
            // Simulate file writing with a sleep
            System.out.println("Writing data to file...");
            String date = getDateAsString();

            for (Price price: prices) {
                if (price.getSupplierName().toLowerCase().contains("craig")) {
                    FileUtil.writeToFile("craigs.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("scott")) {
                    FileUtil.writeToFile("scotts.txt", date, price.getPrice().substring(1));
                }
                if (price.getSupplierName().toLowerCase().contains("campsie")) {
                    FileUtil.writeToFile("campsie.txt", date, price.getPrice().substring(1));
                }
            }
            // Actual file writing logic here
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
