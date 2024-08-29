package com.ml.oilpricechecker.models;

import com.ml.oilpricechecker.enums.RequestType;
import org.springframework.http.MediaType;

import java.util.regex.Pattern;

public class PriceRequest {

    private final String supplierName;
    private final int numberOfLitres;
    private final String urlTemplate;
    private final RequestType requestType;
    private Payload payload;
    private final Pattern pattern;
    private MediaType mediaType;

    public PriceRequest(final String supplierName,
                        final int numberOfLitres,
                        final String urlTemplate,
                        final Pattern pattern,
                        final RequestType requestType) {
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
                        Payload payload,
                        MediaType mediaType) {
        this(supplierName, numberOfLitres, urlTemplate, pattern, requestType);
        this.payload = payload;
        this.mediaType = mediaType;
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

    public Pattern getPattern() {
        return pattern;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    // Method to format the URL with the number of litres
    private String formatUrl(final String urlTemplate, final int numberOfLitres) {
        // Assuming the placeholder is {numberOfLitres}
        return urlTemplate.replace("{numberOfLitres}", String.valueOf(numberOfLitres));
    }
}
