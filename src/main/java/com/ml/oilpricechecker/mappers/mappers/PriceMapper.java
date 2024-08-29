package com.ml.oilpricechecker.mappers.mappers;

import com.ml.oilpricechecker.models.Price;
import com.ml.oilpricechecker.models.PriceResponse;

public final class PriceMapper {

    private PriceMapper() {
    }

    public static final int ONE_HUNDRED = 100;

    public static Price mapPriceResponseToPrice(final PriceResponse priceResponse) {
        return new Price(
                priceResponse.getSupplierName(),
                priceResponse.getPrice(),
                getPencePerLitre(priceResponse));
    }

    private static String getPencePerLitre(final PriceResponse priceResponse) {

        int litres = priceResponse.getNumberOfLitres();

        String price = priceResponse.getPrice().substring(1);
        float cost = Float.parseFloat(price);

        float ppl = (cost / litres) * ONE_HUNDRED;
        return String.format("  (%.1f ppl)", ppl);
    }
}
