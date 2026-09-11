package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.models.weather.WeatherResponse;
import com.ml.oilpricechecker.util.SSLUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class WeatherService {

    private static final String WEATHER_API = "https://api.open-meteo.com/v1/forecast?"
            + "latitude=54.9119&longitude=-7.1535639"
            + "&hourly=temperature_2m&past_days=1&forecast_days=0";

    private final RestTemplate restTemplate;

    @Autowired
    public WeatherService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void getMinAndMaxTemps() throws Exception {
        SSLUtilities.disableSSLCertificateChecking();

        WeatherResponse response = restTemplate.getForObject(WEATHER_API, WeatherResponse.class);

        List<Double> temps = response.getHourly().getTemperature();

        double min = Collections.min(temps);
        double max = Collections.max(temps);

        System.out.println("Min: " + min);
        System.out.println("Max: " + max);

    }
}
