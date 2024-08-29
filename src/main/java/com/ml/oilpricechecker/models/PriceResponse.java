package com.ml.oilpricechecker.models;

public class PriceResponse {
    private String supplierName;
    private String price;
    private int numberOfLitres;

    public PriceResponse(final String supplierName, final String price, final int numberOfLitres) {
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
