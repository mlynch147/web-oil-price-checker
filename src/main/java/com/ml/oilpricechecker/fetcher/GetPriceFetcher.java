package com.ml.oilpricechecker.fetcher;

import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.util.PriceUtilities;
import org.springframework.web.client.RestTemplate;

public class GetPriceFetcher implements PriceFetcher {

    private final RestTemplate restTemplate;

    public GetPriceFetcher(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PriceResponse fetchPrice(final PriceRequest request) {
        String url = request.getUrl();
        String htmlContent = restTemplate.getForObject(url, String.class);

        String extractedText = PriceUtilities.extractPriceFromContent(htmlContent, request.getPattern());
        return new PriceResponse(request.getSupplierName(), extractedText, request.getNumberOfLitres());
    }
}


