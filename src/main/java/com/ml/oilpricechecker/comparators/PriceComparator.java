package com.ml.oilpricechecker.comparators;

import com.ml.oilpricechecker.models.Price;

import java.util.Comparator;

public class PriceComparator implements Comparator<Price> {

    @Override
    public int compare(final Price price1, final Price price2) {
        double p1 = parsePrice(price1.getPrice());
        double p2 = parsePrice(price2.getPrice());

        if (!Double.isNaN(p1) && !Double.isNaN(p2)) {
            int priceComparison = Double.compare(p1, p2);
            if (priceComparison != 0) {
                return priceComparison;
            }
        } else if (!Double.isNaN(p1)) {
            return -1;
        } else if (!Double.isNaN(p2)) {
            return 1;
        }
        return price1.getSupplierName().compareTo(price2.getSupplierName());
    }

    private double parsePrice(final String price) {
        try {
            return Double.parseDouble(price.substring(1));
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
