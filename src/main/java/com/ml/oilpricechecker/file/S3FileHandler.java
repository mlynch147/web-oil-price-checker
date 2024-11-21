package com.ml.oilpricechecker.file;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class S3FileHandler implements IFileHandler {

    @Value("${s3.bucketName:oil-price-checker-data-files}")
    private String bucketName;

    private static final Object LOCK = new Object();

    private final S3Client s3Client;

    public S3FileHandler(final S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void initializeFiles() {
        synchronized (LOCK) {
            try {
                String[] filenames = getInternalFilenames();
                for (String filename : filenames) {
                    try {
                        // Create a HeadObjectRequest to check for the file in the S3 bucket
                        HeadObjectRequest headObjectRequest =
                                HeadObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(filename)
                                        .build();

                        // If file exists, no exception is thrown
                        s3Client.headObject(headObjectRequest);
                    } catch (S3Exception e) {
                        // If NoSuchKeyException is thrown, the file does not exist in the S3 bucket
                        if (e.statusCode() == HttpStatusCode.NOT_FOUND) {
                            copyInternalFileToS3(filename); // File doesn't exist, copy it
                        } else {
                            // For other S3 exceptions, log them
                            System.err.println("Error checking file existence in S3: " + filename);
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeToFile(final String filename, final String newDate, final String newAmount) {
        synchronized (LOCK) {
            try {
                //try and parse the amount before writing...
                Double.parseDouble(newAmount);

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
                    // Manage maximum entries logic (like in your LocalFileHandler)
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

                // Convert the data to a string and upload it to S3
                StringBuilder content = new StringBuilder();
                for (FileData data : dataList) {
                    content.append(data.getDate()).append("=").append(data.getAmount()).append("\n");
                }

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)        // S3 bucket name
                        .key(filename)             // S3 object key (file name)
                        .build();

                // Upload to S3
                s3Client.putObject(putObjectRequest,
                        RequestBody.fromInputStream(
                                new ByteArrayInputStream(content.toString().getBytes(StandardCharsets.UTF_8)),
                                content.length()));

            } catch (NumberFormatException e) {
                System.err.println("Not writing unparsable amount "
                        + "[" + newAmount + "] "
                        + "to file [" + filename + "] "
                        + "on date " + newDate);
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public List<FileData> getCurrentFileContent(final String filename) {
        List<FileData> dataList = new ArrayList<>();
        try {
            // Get the object from S3,
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                            .bucket(bucketName)        // S3 bucket name
                            .key(filename)             // S3 object key (file name)
                            .build();

            InputStream inputStream = s3Client.getObject(getObjectRequest);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] keyValue = line.split("=");
                    if (keyValue.length == 2) {
                        dataList.add(new FileData(keyValue[0], keyValue[1]));
                    }
                }
            }
        } catch (NoSuchKeyException e) {
            System.err.println("File not found in S3 bucket: " + filename);
        } catch (IOException e) {
            System.err.println("Error reading file from S3: " + filename);
            e.printStackTrace();
        }
        return dataList;
    }

    // Method to get the list of filenames from the internal resources directory
    private String[] getInternalFilenames() throws IOException {
        // Assuming files are located in src/main/resources/data/
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/");
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     Objects.requireNonNull(inputStream)))) {
            // Read all filenames from the input stream
            return reader.lines().toArray(String[]::new);
        } catch (NullPointerException e) {
            System.err.println("Resources directory not found.");
            return new String[0];
        }
    }

    private void copyInternalFileToS3(final String filename) {
        try {
            // Assuming the internal files are available as resources
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/" + filename);
            if (inputStream == null) {
                System.err.println("Internal file not found: " + filename);
                return;
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename) // The key is the name of the file in the S3 bucket
                    .build();

            RequestBody requestBody =
                    RequestBody.fromInputStream(inputStream, inputStream.available());

            // Upload the file to S3
            s3Client.putObject(putObjectRequest, requestBody);

            System.out.println("Copied internal file to S3: " + filename);

        } catch (IOException e) {
            System.err.println("Error reading internal file: " + filename);
            e.printStackTrace();
        } catch (S3Exception e) {
            System.err.println("Error uploading file to S3: " + filename);
            e.printStackTrace();
        }
    }
}
