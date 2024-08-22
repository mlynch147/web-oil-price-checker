package com.ml.oilpricechecker.models;

import java.time.LocalDate;

public class PriceResponse {
    private String supplierName;
    private String price;
    private int numberOfLitres;

    public PriceResponse(String supplierName, String price, int numberOfLitres) {
        this.supplierName = supplierName;
        this.price = price;
        this.numberOfLitres = numberOfLitres;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getPrice() {
        return price;
    }

    public int getNumberOfLitres() {
        return numberOfLitres;
    }
}
