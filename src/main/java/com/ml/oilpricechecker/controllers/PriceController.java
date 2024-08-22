package com.ml.oilpricechecker.controllers;

import com.ml.oilpricechecker.models.Price;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PriceController {

     @GetMapping("/prices")
     public List<Price> getPrices() {
         // Create dummy static data
         List<Price> data = new ArrayList<>();

         data.add(new Price("Craigs", "£100.00", "(50.1)"));
         data.add(new Price("Scotts", "£110.00", "(51.1)"));
         data.add(new Price("Joes", "£105.00", "(50.5)"));

         return data;
     }
}

