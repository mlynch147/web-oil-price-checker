package com.ml.oilpricechecker.models;

public class WeeklyComparison {

    private String supplierName;
    private double todaysPrice;
    private double weekOldPrice;
    private double priceDifference;

    public WeeklyComparison(final String supplierName, final String todaysPrice, final String weekOldPrice) {
        this.supplierName = supplierName;
        this.todaysPrice = Double.parseDouble(todaysPrice);
        this.weekOldPrice = Double.parseDouble(weekOldPrice);
        this.priceDifference = this.todaysPrice - this.weekOldPrice;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(final String supplierName) {
        this.supplierName = supplierName;
    }

    public double getTodaysPrice() {
        return todaysPrice;
    }

    public void setTodaysPrice(final double todaysPrice) {
        this.todaysPrice = todaysPrice;
    }

    public double getWeekOldPrice() {
        return weekOldPrice;
    }

    public void setWeekOldPrice(final double weekOldPrice) {
        this.weekOldPrice = weekOldPrice;
    }

    public double getPriceDifference() {
        return priceDifference;
    }

}
