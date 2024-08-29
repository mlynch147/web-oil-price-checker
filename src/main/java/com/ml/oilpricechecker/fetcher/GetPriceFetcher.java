package com.ml.oilpricechecker.fetcher;

import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetPriceFetcher implements PriceFetcher {

    private final RestTemplate restTemplate;

    public GetPriceFetcher(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PriceResponse fetchPrice(final PriceRequest request) {
        String url = request.getUrl();
        String htmlContent = restTemplate.getForObject(url, String.class);

        String extractedText = extractPriceFromContent(htmlContent, request.getPattern());
        return new PriceResponse(request.getSupplierName(), extractedText, request.getNumberOfLitres());
    }

    private String extractPriceFromContent(final String content, final Pattern pattern) {
        if (content == null) {
            return "N/A";
        }
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String extractedText = "£" + matcher.group(1);
            if (!extractedText.contains(".")) {
                extractedText += ".00";
            }
            return extractedText;
        }
        return "N/A";
    }
}


