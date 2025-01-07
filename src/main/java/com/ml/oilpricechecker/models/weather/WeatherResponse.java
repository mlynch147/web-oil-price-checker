package com.ml.oilpricechecker.models.weather;

public class WeatherResponse {
    private Hourly hourly;

    public Hourly getHourly() {
        return hourly;
    }

    public void setHourly(final Hourly hourly) {
        this.hourly = hourly;
    }
}

