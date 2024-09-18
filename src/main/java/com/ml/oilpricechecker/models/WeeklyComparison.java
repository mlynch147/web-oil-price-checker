package com.ml.oilpricechecker.models;

public class WeeklyComparison {

    private String supplierName;
    private double todaysPrice;
    private double weekOldPrice;

    public WeeklyComparison(final String supplierName, final double todaysPrice, final double weekOldPrice) {
        this.supplierName = supplierName;
        this.todaysPrice = todaysPrice;
        this.weekOldPrice = weekOldPrice;
    }

    public WeeklyComparison(final String supplierName, final String todaysPrice, final String weekOldPrice) {
        this.supplierName = supplierName;
        this.todaysPrice = Double.parseDouble(todaysPrice);
        this.weekOldPrice = Double.parseDouble(weekOldPrice);
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public double getTodaysPrice() {
        return todaysPrice;
    }

    public void setTodaysPrice(double todaysPrice) {
        this.todaysPrice = todaysPrice;
    }

    public double getWeekOldPrice() {
        return weekOldPrice;
    }

    public void setWeekOldPrice(double weekOldPrice) {
        this.weekOldPrice = weekOldPrice;
    }
}
