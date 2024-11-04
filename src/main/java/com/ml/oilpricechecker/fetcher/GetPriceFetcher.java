package com.ml.oilpricechecker.fetcher;

import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.util.PriceUtilities;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class GetPriceFetcher implements PriceFetcher {

    public static final int MAX_TIMEOUT_IN_MILLISECONDS = 5000;
    private final RestTemplate restTemplate;

    public GetPriceFetcher(final RestTemplate restTemplate) {
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

    @Override
    public PriceResponse fetchPrice(final PriceRequest request) {
        String extractedText = "N/A";
        try {
            String url = request.getUrl();
            String htmlContent = restTemplate.getForObject(url, String.class);

            extractedText = PriceUtilities.extractPriceFromContent(htmlContent, request.getPattern());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP Exception: " + e.getStatusCode());
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }
        return new PriceResponse(request.getSupplierName(), extractedText, request.getNumberOfLitres());
    }
}


