package com.ml.oilpricechecker.file;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Primary
@Component
public class LocalFileHandler implements IFileHandler {

    private static final Object LOCK = new Object();

    // External directory path
    private static final String EXTERNAL_BASE_PATH = System.getProperty("user.home")
            + "/Library/Application Support/oilpricechecker/";

    // Method to initialize and copy all files from resources to external directory
    @Override
    public void initializeFiles() {
        synchronized (LOCK) {
            try {
                // List of filenames in the internal resources
                String[] filenames = getInternalFilenames();
                for (String filename : filenames) {
                    Path externalFilePath = Paths.get(EXTERNAL_BASE_PATH, filename);
                    if (!Files.exists(externalFilePath)) {
                        copyInternalFileToExternal(filename);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to write data to an external file
    @Override
    public void writeToFile(final String filename, final String newDate, final String newAmount) {
        synchronized (LOCK) {
            try {
                //try and parse the amount before writing...
                Double.parseDouble(newAmount);

                Path externalFilePath = Paths.get(EXTERNAL_BASE_PATH, filename);
                List<FileData> dataList = getCurrentFileContent(filename);

                String newDateTrimmed = newDate.trim();
                String newAmountTrimmed = newAmount.trim();
                boolean keyExists = false;

                for (FileData data : dataList) {
                    if (data.getDate().equals(newDateTrimmed)) {
                        data.setAmount(newAmountTrimmed);
                        keyExists = true;
                        break;
                    }
                }

                if (!keyExists) {
                    // Manage maximum entries logic
                    if (filename.contains("weekly_comparison_")) {
                        if (dataList.size() >= MAX_WEEKLY_COMPARISON_DAYS) {
                            dataList.remove(0);
                        }
                    } else if (filename.contains("six_months_")) {
                        if (dataList.size() >= MAX_SIX_MONTH_ENTRIES) {
                            dataList.remove(0);
                        }
                    } else {
                        if (dataList.size() >= MAX_CHART_DATA_DAYS) {
                            dataList.remove(0);
                        }
                    }
                    dataList.add(new FileData(newDateTrimmed, newAmountTrimmed));
                }

                // Write the updated data to the external file
                try (BufferedWriter writer = Files.newBufferedWriter(externalFilePath)) {
                    for (FileData data : dataList) {
                        String line = data.getDate() + "=" + data.getAmount();
                        writer.write(line);
                        writer.newLine();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.err.println("Not writing unparsable amount "
                        + "[" + newAmount + "] "
                         + "to file [" + filename + "] "
                         + "on date "  + newDate);
                System.err.println(e.getMessage());
            }
        }
    }

    // Method to get the current content of an external file
    @Override
    public List<FileData> getCurrentFileContent(final String filename) {
        List<FileData> dataList = new ArrayList<>();
        Path externalFilePath = Paths.get(EXTERNAL_BASE_PATH, filename);

        if (!Files.exists(externalFilePath) && isValidFileName(filename)) {
            try {
                // File doesn't exist, so let's create it
                Files.createFile(externalFilePath);
                System.out.println("File created: " + externalFilePath);
            } catch (IOException e) {
                System.err.println("Failed to create the file: " + e.getMessage());
                return dataList;  // Returning an empty list if file creation fails
            }
        }

        try (BufferedReader reader = Files.newBufferedReader(externalFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keyValue = line.split("=");
                dataList.add(new FileData(keyValue[0], keyValue[1]));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    // Method to get the list of filenames from the internal resources directory
    private String[] getInternalFilenames() throws IOException {
        // Assuming files are located in src/main/resources/data/
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/");
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            // Read all filenames from the input stream
            return reader.lines().toArray(String[]::new);
        } catch (NullPointerException e) {
            System.err.println("Resources directory not found.");
            return new String[0];
        }
    }


    // Method to copy the internal file from the resources to the external directory
    private static void copyInternalFileToExternal(final String filename) {
        Path externalFilePath = Paths.get(EXTERNAL_BASE_PATH, filename);

        // Create the external directory if it does not exist
        try {
            Files.createDirectories(externalFilePath.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create external directory: " + externalFilePath.getParent());
            e.printStackTrace();
            return;  // Exit if unable to create directory
        }

        try (InputStream inputStream = LocalFileHandler.class.getClassLoader().getResourceAsStream("data/" + filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter writer = Files.newBufferedWriter(externalFilePath)) {

            if (inputStream == null) {
                System.err.println("Internal file not found in resources: " + filename);
                return;  // Exit if the file is not found
            }

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Copied internal file to external: " + filename);

        } catch (IOException e) {
            System.err.println("Error copying internal file to external: " + filename);
            e.printStackTrace();
        }
    }

    //TODO - build some rules...
    private boolean isValidFileName(final String filename) {
        //for now return true...
        return true;
    }
}
