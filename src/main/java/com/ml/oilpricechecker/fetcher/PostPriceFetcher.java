package com.ml.oilpricechecker.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.oilpricechecker.models.NichollOilsFuelModel;
import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostPriceFetcher implements PriceFetcher {
    private final RestTemplate restTemplate;

    public PostPriceFetcher(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PriceResponse fetchPrice(PriceRequest request) {
        String extractedText = "N/A";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(request.getMediaType());

            HttpEntity<Object> requestEntity = createRequestEntity(request);
            String htmlContent = restTemplate.postForObject(request.getUrl(), requestEntity, String.class);

            extractedText = extractPriceFromContent(htmlContent, request.getPattern());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            extractedText = "HTTP Exception: " + e.getStatusCode();
        } catch (Exception e) {
            extractedText = "Exception occurred: " + e.getMessage();
        }
        return new PriceResponse(request.getSupplierName(), extractedText, request.getNumberOfLitres());
    }

    private HttpEntity<Object> createRequestEntity(PriceRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(request.getMediaType());

        if (request.getMediaType().equals(MediaType.APPLICATION_JSON)) {
            ObjectMapper objectMapper = new ObjectMapper();
            String postData = objectMapper.writeValueAsString(new NichollOilsFuelModel("500"));
            return new HttpEntity<>(postData, headers);
        } else {
            return new HttpEntity<>(request.getPayload().getFormData(), headers);
        }
    }

    private String extractPriceFromContent(String content, Pattern pattern) {
        if (content == null) return "N/A";
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String extractedText = "£" + matcher.group(1);
            if (!extractedText.contains(".")) extractedText += ".00";
            return extractedText;
        }
        return "N/A";
    }
}