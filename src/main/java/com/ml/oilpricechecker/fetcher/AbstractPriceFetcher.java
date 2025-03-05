package com.ml.oilpricechecker.fetcher;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractPriceFetcher implements PriceFetcher {

    public static final int MAX_TIMEOUT_IN_MILLISECONDS = 5000;
    protected final RestTemplate restTemplate;

    public AbstractPriceFetcher(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        setTimeouts();
    }

    private void setTimeouts() {
        if (this.restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
            SimpleClientHttpRequestFactory requestFactory
                    = (SimpleClientHttpRequestFactory) this.restTemplate.getRequestFactory();
            requestFactory.setConnectTimeout(MAX_TIMEOUT_IN_MILLISECONDS);
            requestFactory.setReadTimeout(MAX_TIMEOUT_IN_MILLISECONDS);
        }
    }

}
