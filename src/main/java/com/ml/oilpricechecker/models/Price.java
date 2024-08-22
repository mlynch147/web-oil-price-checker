package com.ml.oilpricechecker.models;

public class Price
{
    private String supplierName;
    private String price;
    private String pencePerLitre;

    public Price(String supplierName, String price, String pencePerLitre) {
        this.supplierName = supplierName;
        this.price = price;
        this.pencePerLitre = pencePerLitre;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPencePerLitre() {
        return pencePerLitre;
    }

    public void setPencePerLitre(String pencePerLitre) {
        this.pencePerLitre = pencePerLitre;
    }
}
