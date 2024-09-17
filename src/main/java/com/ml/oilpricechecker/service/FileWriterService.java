package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.models.Price;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
            Thread.sleep(MILLIS);
            System.out.println("Writing data to file...");
            // Actual file writing logic here
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }
}
