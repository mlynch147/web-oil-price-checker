package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.models.BulkDiscountPoint;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.models.SupplierBulkDiscount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the "bulk discount curve": pence per litre plotted against order
 * volume for every supplier.
 *
 * <p>Producing the curve means asking each supplier for every volume, so the
 * result is cached. Without the cache a single page load would trigger ten
 * full scrapes of every supplier site.</p>
 */
@Service
public class BulkDiscountService {

    private static final int MIN_LITRES = 100;
    private static final int MAX_LITRES = 1000;
    private static final int LITRE_STEP = 100;
    private static final int PENCE_IN_A_POUND = 100;
    private static final long CACHE_MINUTES = 30;
    private static final String UNAVAILABLE = "N/A";

    private final PriceService priceService;

    private List<SupplierBulkDiscount> cachedCurve;
    private Instant cachedAt;

    @Autowired
    public BulkDiscountService(final PriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * Returns the bulk discount curve, refreshing it only when the cached copy
     * has expired.
     */
    public synchronized List<SupplierBulkDiscount> getBulkDiscountCurve() throws Exception {

        if (isCacheValid()) {
            return cachedCurve;
        }

        // Keyed by supplier so points stay grouped, ordered by volume ascending.
        Map<String, List<BulkDiscountPoint>> pointsBySupplier = new LinkedHashMap<>();

        // Deliberately sequential: PriceService already fans out across suppliers
        // in parallel for each volume, and running the volumes in parallel too
        // would exhaust the shared thread pool and hammer the supplier sites.
        for (int litres = MIN_LITRES; litres <= MAX_LITRES; litres += LITRE_STEP) {

            List<PriceResponse> responses = priceService.getCurrentPrices(litres);

            for (PriceResponse response : responses) {
                Double cost = parseCost(response.getPrice());

                if (cost == null) {
                    continue;
                }

                pointsBySupplier
                        .computeIfAbsent(response.getSupplierName(), key -> new ArrayList<>())
                        .add(new BulkDiscountPoint(litres, cost, toPencePerLitre(cost, litres)));
            }
        }

        List<SupplierBulkDiscount> curve = new ArrayList<>();

        for (Map.Entry<String, List<BulkDiscountPoint>> entry : pointsBySupplier.entrySet()) {
            curve.add(new SupplierBulkDiscount(entry.getKey(), entry.getValue()));
        }

        cachedCurve = curve;
        cachedAt = Instant.now();

        return curve;
    }

    private boolean isCacheValid() {
        return cachedCurve != null
                && cachedAt != null
                && Duration.between(cachedAt, Instant.now()).toMinutes() < CACHE_MINUTES;
    }

    private double toPencePerLitre(final double cost, final int litres) {
        return (cost / litres) * PENCE_IN_A_POUND;
    }

    /**
     * Supplier prices arrive as "£312.50", or "N/A" when the scrape failed.
     */
    private Double parseCost(final String price) {

        if (price == null || price.isEmpty() || price.equals(UNAVAILABLE)) {
            return null;
        }

        try {
            return Double.parseDouble(price.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

