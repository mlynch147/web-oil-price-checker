package com.ml.oilpricechecker.controllers;

import com.ml.oilpricechecker.comparators.PriceComparator;
import com.ml.oilpricechecker.mappers.mappers.PriceMapper;
import com.ml.oilpricechecker.models.Price;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.service.FileWriterService;
import com.ml.oilpricechecker.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class PriceController {

    public static final int DEFAULT_NUMBER_OF_LITRES = 500;
    public static final int MIN_NUMBER_OF_LITRES = 100;
    public static final int MAX_NUMBER_OF_LITRES = 1000;

    private final PriceService priceService;
    private final FileWriterService fileWriterService;

    @Autowired
    public PriceController(final PriceService priceService, final FileWriterService fileWriterService) {
        this.priceService = priceService;
        this.fileWriterService = fileWriterService;
    }

     @GetMapping("/prices/{litre}")
     public List<Price> getPrices(@PathVariable("litre") final String litre) throws Exception {

        int numberOfLitres = sanitiseLitres(litre);

        List<Price> data = new ArrayList<>();

         List<PriceResponse> pricesResponses =
                 priceService.getCurrentPrices(numberOfLitres);

         for (PriceResponse priceResponse: pricesResponses) {
             data.add(PriceMapper.mapPriceResponseToPrice(priceResponse));
         }

         Collections.sort(data, new PriceComparator());

         if (numberOfLitres == DEFAULT_NUMBER_OF_LITRES) {
             fileWriterService.writePricesToFile(data);
         }

         return data;
     }

    private int sanitiseLitres(final String litre) {

        int litres = DEFAULT_NUMBER_OF_LITRES;

        try {
            litres = Integer.parseInt(litre);

            if (litres < MIN_NUMBER_OF_LITRES || litres > MAX_NUMBER_OF_LITRES) {
                litres = DEFAULT_NUMBER_OF_LITRES;
            }
        } catch (Exception e) {
            litres = DEFAULT_NUMBER_OF_LITRES;
        }
        return litres;
    }
}

