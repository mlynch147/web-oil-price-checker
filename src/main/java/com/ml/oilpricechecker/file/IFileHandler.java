package com.ml.oilpricechecker.file;

import java.util.List;

public interface IFileHandler {

    int MAX_CHART_DATA_DAYS = 14;
    int MAX_WEEKLY_COMPARISON_DAYS = 8;
    int MAX_SIX_MONTH_ENTRIES = 26;

    // Method to initialize and copy all files from resources to external directory
    void initializeFiles();

    // Method to write data to an external file
    void writeToFile(String filename, String newDate, String newAmount);

    // Method to get the current content of an external file
    List<FileData> getCurrentFileContent(String filename);

    void rewriteFile(String filename, List<FileData> fileData);
}
