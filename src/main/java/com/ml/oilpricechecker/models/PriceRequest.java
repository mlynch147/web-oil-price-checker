package com.ml.oilpricechecker.models;

import com.ml.oilpricechecker.enums.RequestType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.regex.Pattern;

public class PriceRequest {

    private String supplierName;
    private int numberOfLitres;
    private String urlTemplate;
    private RequestType requestType;
    private Payload payload;
    private Pattern pattern;


    public PriceRequest(String supplierName,
                        int numberOfLitres,
                        String urlTemplate,
                        Pattern pattern,
                        RequestType requestType) {
        this.supplierName = supplierName;
        this.numberOfLitres = numberOfLitres;
        this.urlTemplate = urlTemplate;
        this.pattern = pattern;
        this.requestType = requestType;
    }

    public PriceRequest(String supplierName,
                        int numberOfLitres,
                        String urlTemplate,
                        Pattern pattern,
                        RequestType requestType,
                        Payload payload) {
        this(supplierName, numberOfLitres, urlTemplate, pattern, requestType);
        this.payload = payload;
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

    public RequestType getRequestType() {
        return requestType;
    }

    public Payload getPayload() {
        return payload;
    }

    public void addFormData(String key, String value) {
        payload.formData.add(key, value);
    }

    public Pattern getPattern() {
        return pattern;
    }

    // Method to format the URL with the number of litres
    private String formatUrl(String urlTemplate, int numberOfLitres) {
        // Assuming the placeholder is {numberOfLitres}
        return urlTemplate.replace("{numberOfLitres}", String.valueOf(numberOfLitres));
    }
}
