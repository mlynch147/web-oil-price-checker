package com.ml.oilpricechecker.controllers;

import com.ml.oilpricechecker.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    public WeatherService weatherService;

    @Autowired
    public WeatherController(final WeatherService weatherService) {
        this.weatherService = weatherService;
    }

     @GetMapping("/weather")
     public void getWeatherData() throws Exception {
        weatherService.getMinAndMaxTemps();
     }
}

