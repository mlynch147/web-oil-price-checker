package com.ml.oilpricechecker.file;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FileUtil {

    private FileUtil() {
    }

    private static final Object LOCK = new Object();
    public static final int MAX_CHART_DATA_DAYS = 14;
    public static final int MAX_WEEKLY_COMPARISON_DAYS = 8;
    private static final String BASE_PATH = "src/main/data/";

    // Method to write data to a file in internal storage
    public static void writeToFile(final String filename, final String newDate, final String newAmount) {
        synchronized (LOCK) {

            Path filePath = Paths.get(BASE_PATH, filename);

            List<FileData> dataList = getCurrentFileContent(filename);

            String newDateTrimmed = newDate.trim();
            String newAmountTrimmed = newAmount.trim();

            // Check if the key (date) already exists in the list
            boolean keyExists = false;
            for (FileData data : dataList) {
                if (data.getDate().equals(newDateTrimmed)) {
                    // Update the existing entry
                    data.setAmount(newAmountTrimmed);
                    keyExists = true;
                    break;
                }
            }

            // If the key does not exist, add a new entry
            if (!keyExists) {
                // Remove the first entry if there are already 14 entries

                if (filename.contains("weekly_comparison_")) {
                    if (dataList.size() >= MAX_WEEKLY_COMPARISON_DAYS) {
                        dataList.remove(0);
                    }
                } else {
                    if (dataList.size() >= MAX_CHART_DATA_DAYS) {
                        dataList.remove(0);
                    }
                }

                // Add the new data
                dataList.add(new FileData(newDateTrimmed, newAmountTrimmed));
            }

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                // Loop through the dataList and write each key-value pair to the file
                for (FileData data : dataList) {
                    String line = data.getDate() + "=" + data.getAmount();
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<FileData> getCurrentFileContent(final String filename) {
        List<FileData> dataList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Path filePath = Paths.get(BASE_PATH, filename);

            try (BufferedReader reader = Files.newBufferedReader(filePath)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] keyValue = line.split("=");
                    dataList.add(new FileData(keyValue[0], keyValue[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

}
