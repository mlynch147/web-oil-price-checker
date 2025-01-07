package com.ml.oilpricechecker.models.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Hourly {

    @JsonProperty("temperature_2m")
    private List<Double> temperature;

    public List<Double> getTemperature() {
        return temperature;
    }

    public void setTemperature(final List<Double> temperature) {
        this.temperature = temperature;
    }
}
