package com.ml.oilpricechecker.config;

import java.util.Map;

public class PriceRequestConfig {
    private String name;
    private String url;
    private String pattern;
    private String alternatePattern;
    private String requestType;
    private String mediaType;
    private Map<String, String> payload;
    private String amountMapper;

    // Getters and setters...
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(final String url) {
        this.url = url;
    }
    public String getPattern() {
        return pattern;
    }
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
    public String getAlternatePattern() {
        return alternatePattern;
    }
    public void setAlternatePattern(final String alternatePattern) {
        this.alternatePattern = alternatePattern;
    }
    public String getRequestType() {
        return requestType;
    }
    public void setRequestType(final String requestType) {
        this.requestType = requestType;
    }
    public String getMediaType() {
        return mediaType;
    }
    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }
    public Map<String, String> getPayload() {
        return payload;
    }
    public void setPayload(final Map<String, String> payload) {
        this.payload = payload;
    }

    public String getAmountMapper() {
        return amountMapper;
    }

    public void setAmountMapper(String amountMapper) {
        this.amountMapper = amountMapper;
    }
}
