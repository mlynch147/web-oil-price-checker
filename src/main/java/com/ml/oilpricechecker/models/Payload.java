package com.ml.oilpricechecker.models;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class Payload {

    private final MultiValueMap<String, String> formData;

    public Payload() {
        formData = new LinkedMultiValueMap<>();
    }

    public MultiValueMap<String, String> getFormData() {
        return formData;
    }

    public void add(final String key, final String value) {
        formData.add(key, value);
    }
}
