package com.ml.oilpricechecker.models;

/**
 * A single point on a supplier's bulk discount curve: what one order volume
 * costs in total, and what that works out at per litre.
 */
public class BulkDiscountPoint {

    private final int numberOfLitres;
    private final double cost;
    private final double pencePerLitre;

    public BulkDiscountPoint(final int numberOfLitres, final double cost, final double pencePerLitre) {
        this.numberOfLitres = numberOfLitres;
        this.cost = cost;
        this.pencePerLitre = pencePerLitre;
    }

    public int getNumberOfLitres() {
        return numberOfLitres;
    }

    public double getCost() {
        return cost;
    }

    public double getPencePerLitre() {
        return pencePerLitre;
    }
}

