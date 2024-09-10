package com.ml.oilpricechecker.file;

import java.util.List;

public class ChartDataWithName {

    private List<ChartData> chartData;
    private String supplierName;

    public ChartDataWithName(
            final List<ChartData> chartData, final String supplierName) {
        this.chartData = chartData;
        this.supplierName = supplierName;
    }

    public List<ChartData> getChartData() {
        return chartData;
    }

    public void setChartData(final List<ChartData> chartData) {
        this.chartData = chartData;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(final String supplierName) {
        this.supplierName = supplierName;
    }
}
