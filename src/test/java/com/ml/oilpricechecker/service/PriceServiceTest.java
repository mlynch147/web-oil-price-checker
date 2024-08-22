package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.models.PriceResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PriceServiceTest {

    private PriceService priceService;

    @Test
    public void testConcurrentCalls() throws Exception {
        priceService = new PriceService();

        List<PriceResponse> responses = priceService.makeConcurrentHttpCalls();

        for (PriceResponse response: responses) {
            System.out.println("Name: " + response.getSupplierName());
            System.out.println("Price: " + response.getPrice());
            System.out.println("Date: " + response.getCurrentDate());
            System.out.println("------------------------------");
        }
    }
}
