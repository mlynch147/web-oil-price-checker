package com.ml.oilpricechecker.mappers.mappers;

import com.ml.oilpricechecker.models.Price;
import com.ml.oilpricechecker.models.PriceResponse;

public class PriceMapper {

    public static Price MapPriceResponseToPrice(PriceResponse priceResponse) {
        return new Price(
                priceResponse.getSupplierName(),
                priceResponse.getPrice(),
                getPencePerLitre(priceResponse));
    }

    private static String getPencePerLitre(PriceResponse priceResponse) {

        int litres = priceResponse.getNumberOfLitres();

        String price = priceResponse.getPrice().substring(1);
        float cost = Float.parseFloat(price);

        float ppl = (cost / litres) * 100;
        return String.format("  (%.1f ppl)", ppl);
    }
}
