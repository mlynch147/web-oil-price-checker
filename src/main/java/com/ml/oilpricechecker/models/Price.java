package com.ml.oilpricechecker.models;

public class Price {
    private String supplierName;
    private String price;
    private String pencePerLitre;

    public Price(final String supplierName, final String price, final String pencePerLitre) {
        this.supplierName = supplierName;
        this.price = price;
        this.pencePerLitre = pencePerLitre;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(final String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public String getPencePerLitre() {
        return pencePerLitre;
    }

    public void setPencePerLitre(final String pencePerLitre) {
        this.pencePerLitre = pencePerLitre;
    }
}
