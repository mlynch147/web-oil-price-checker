package com.ml.oilpricechecker.file;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
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

    // Method to write data to a file in internal storage
    public static void writeToFile(final String filename, final String newDate, final String newAmount) {
        synchronized (LOCK) {
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

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                // Write each entry to the file
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

    // Method to read data from a file in internal storage
    private static List<FileData> getCurrentFileContent(final String filename) {
        synchronized (LOCK) {
            List<FileData> dataList = new ArrayList<>();

            // Check if the file exists before reading its content
            File file = new File(filename);
            if (!file.exists()) {
                // Create an empty file if it doesn't exist
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Read from the existing file
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        FileData data = parseFileData(line);
                        dataList.add(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return dataList;
        }
    }


    // Method to parse a string into ChartData
    private static FileData parseFileData(final String dataString) {
        // Assuming the format is "dd/MM/yyyy=value"
        String[] parts = dataString.split("=");
        String dateString = parts[0].trim();
        String value = parts[1].trim();
        return new FileData(dateString, value);
    }


    // Method to read data from a file in internal storage and create a list of ChartData objects
    public static List<ChartData> readFromFile(final String filename) {
        synchronized (LOCK) {
            List<ChartData> dataList = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String dateString = parts[0];
                        double value = Double.parseDouble(parts[1]);

                        // Assuming you have a constructor in ChartData that takes a Date and an int
                        ChartData chartData = new ChartData(dateFormat.parse(dateString), value);

                        dataList.add(chartData);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dataList;
        }
    }

    public static ChartDataWithName readDataFromFile(final String fileName, final String displayName) {
        List<ChartData> dataList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            ClassPathResource resource = new ClassPathResource("data/" + fileName);
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] keyValue = line.split("=");
                String date = keyValue[0];

                String amount = keyValue[1];
                System.out.println(amount);
                double value = Double.parseDouble(keyValue[1]);

                ChartData chartData = new ChartData(dateFormat.parse(date), value);

                dataList.add(chartData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ChartDataWithName(dataList, displayName);
    }
}
