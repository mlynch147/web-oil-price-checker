package com.ml.oilpricechecker.models;

import com.ml.oilpricechecker.enums.RequestType;

public class PriceRequest {

    private String supplierName;
    private int numberOfLitres;
    private String urlTemplate;
    private RequestType requestType;
    private String regex;

    public PriceRequest(String supplierName,
                        int numberOfLitres,
                        String urlTemplate,
                        String regex,
                        RequestType requestType) {
        this.supplierName = supplierName;
        this.numberOfLitres = numberOfLitres;
        this.urlTemplate = urlTemplate;
        this.regex = regex;
        this.requestType = requestType;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getNumberOfLitres() {
        return numberOfLitres;
    }

    public String getUrl() {
        return formatUrl(urlTemplate, numberOfLitres);
    }

    public String getRegex() {
        return regex;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    // Method to format the URL with the number of litres
    private String formatUrl(String urlTemplate, int numberOfLitres) {
        // Assuming the placeholder is {numberOfLitres}
        return urlTemplate.replace("{numberOfLitres}", String.valueOf(numberOfLitres));
    }
}
