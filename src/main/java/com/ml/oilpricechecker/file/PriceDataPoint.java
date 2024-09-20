package com.ml.oilpricechecker.file;

import java.time.LocalDate;

public class PriceDataPoint {

    private LocalDate date;
    private double value;

    public PriceDataPoint(final LocalDate date, final double value) {
        this.date = date;
        this.value = value;

        date.atTime(0, 0, 0);
    }

    public LocalDate getDate() {
        return  date;
    }

    public double getValue() {
        return value;
    }
}
