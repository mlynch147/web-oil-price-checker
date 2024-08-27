package com.ml.oilpricechecker.models;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class Payload {

    MultiValueMap<String, String> formData;

    public Payload() {
        formData = new LinkedMultiValueMap<>();
    }

    public MultiValueMap<String, String> getFormData() {
        return formData;
    }

    public void add(String key, String value) {
        formData.add(key, value);
    }
}
