package com.ml.oilpricechecker.models;

import java.time.LocalDate;

public class PriceResponse {
    private String supplierName;
    private String price;
    private LocalDate currentDate;

    public PriceResponse(String supplierName, String price) {
        this.supplierName = supplierName;
        this.price = price;
        this.currentDate = LocalDate.now(); // Automatically set to the current date
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getPrice() {
        return price;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }
}
