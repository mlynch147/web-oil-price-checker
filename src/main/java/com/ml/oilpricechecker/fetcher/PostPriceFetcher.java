package com.ml.oilpricechecker.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.oilpricechecker.models.NichollOilsFuelModel;
import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.util.PriceUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class PostPriceFetcher implements PriceFetcher {
    private final RestTemplate restTemplate;

    public PostPriceFetcher(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PriceResponse fetchPrice(final PriceRequest request) {
        String extractedText = "N/A";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(request.getMediaType());

            HttpEntity<Object> requestEntity = createRequestEntity(request);
            String htmlContent = restTemplate.postForObject(request.getUrl(), requestEntity, String.class);

            extractedText = PriceUtilities.extractPriceFromContent(htmlContent, request.getPattern());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP Exception: " + e.getStatusCode());
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }
        return new PriceResponse(request.getSupplierName(), extractedText, request.getNumberOfLitres());
    }

    private HttpEntity<Object> createRequestEntity(final PriceRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(request.getMediaType());

        if (request.getMediaType().equals(MediaType.APPLICATION_JSON)) {
            ObjectMapper objectMapper = new ObjectMapper();
            String postData = objectMapper.writeValueAsString(
                    new NichollOilsFuelModel(request.getNumberOfLitres()));
            return new HttpEntity<>(postData, headers);
        } else {
            return new HttpEntity<>(request.getPayload().getFormData(), headers);
        }
    }

}
