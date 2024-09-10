package com.ml.oilpricechecker.file;

import java.util.List;

public class SupplierPriceData {

    private List<PriceDataPoint> priceDataPoints;
    private String supplierName;

    public SupplierPriceData(
            final List<PriceDataPoint> priceDataPoints, final String supplierName) {
        this.priceDataPoints = priceDataPoints;
        this.supplierName = supplierName;
    }

    public List<PriceDataPoint> getPriceDataPoints() {
        return priceDataPoints;
    }

    public void setPriceDataPoints(
            final List<PriceDataPoint> priceDataPoints) {
        this.priceDataPoints = priceDataPoints;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(final String supplierName) {
        this.supplierName = supplierName;
    }
}
