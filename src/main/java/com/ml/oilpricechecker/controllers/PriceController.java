package com.ml.oilpricechecker.controllers;

import com.ml.oilpricechecker.mappers.mappers.PriceMapper;
import com.ml.oilpricechecker.models.Price;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.service.PriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PriceController {

     @GetMapping("/prices")
     public List<Price> getPrices() throws Exception{
         // Create dummy static data
         List<Price> data = new ArrayList<>();

         //hack
         PriceService priceService = new PriceService();
         List<PriceResponse> pricesResponses = priceService.makeConcurrentHttpCalls();

         for (PriceResponse priceResponse: pricesResponses) {
             data.add(PriceMapper.MapPriceResponseToPrice(priceResponse));
         }

/*
         data.add(new Price("Craigs", "£100.00", "(50.1)"));
         data.add(new Price("Scotts", "£110.00", "(51.1)"));
         data.add(new Price("Joes", "£105.00", "(50.5)"));
*/

         return data;
     }
}

