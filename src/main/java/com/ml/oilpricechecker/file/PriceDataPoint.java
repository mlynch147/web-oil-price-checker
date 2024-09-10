package com.ml.oilpricechecker.file;

import java.util.Date;

public class PriceDataPoint {

    private Date date;
    private double value;

    public PriceDataPoint(final Date date, final double value) {
        this.date = date;
        this.value = value;

        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
    }

    public Date getDate() {
        return  date;
    }

    public double getValue() {
        return value;
    }
}
