package com.ml.oilpricechecker.controllers;

import com.ml.oilpricechecker.mappers.mappers.PriceMapper;
import com.ml.oilpricechecker.models.Price;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PriceController {

    public static final int DEFAULT_NUMBER_OF_LITRES = 500;
    private final PriceService priceService;

    @Autowired
    public PriceController(final PriceService priceService) {
        this.priceService = priceService;
    }

     @GetMapping("/prices/{litre}")
     public List<Price> getPrices(@PathVariable("litre") final String litre) throws Exception {
         List<Price> data = new ArrayList<>();

         List<PriceResponse> pricesResponses =
                 priceService.getCurrentPrices(Integer.valueOf(litre));

         for (PriceResponse priceResponse: pricesResponses) {
             data.add(PriceMapper.mapPriceResponseToPrice(priceResponse));
         }

         return data;
     }
}

