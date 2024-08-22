package com.ml.oilpricechecker.models;

import java.time.LocalDate;

public class PriceResponse {
    private String supplierName;
    private String price;

    public PriceResponse(String supplierName, String price) {
        this.supplierName = supplierName;
        this.price = price;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getPrice() {
        return price;
    }
}
