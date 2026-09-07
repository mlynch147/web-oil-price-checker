package com.ml.oilpricechecker.models;

import java.util.List;

/**
 * One supplier's pence-per-litre curve across the supported order volumes.
 */
public class SupplierBulkDiscount {

    private final String supplierName;
    private final List<BulkDiscountPoint> points;

    public SupplierBulkDiscount(final String supplierName, final List<BulkDiscountPoint> points) {
        this.supplierName = supplierName;
        this.points = points;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public List<BulkDiscountPoint> getPoints() {
        return points;
    }
}

