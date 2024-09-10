package com.ml.oilpricechecker.file;

public class FileData {

    public FileData(final String date, final String amount) {
        this.date = date.trim();
        this.amount = amount.trim();
    }

    private String date;

    private String amount;

    public String getDate() {
        return date.trim();
    }

    public String getAmount() {
        return amount.trim();
    }

    public void setAmount(final String amount) {
        this.amount = amount.trim();
    }


}
