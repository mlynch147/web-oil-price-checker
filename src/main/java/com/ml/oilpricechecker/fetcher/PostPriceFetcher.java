package com.ml.oilpricechecker.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.oilpricechecker.models.NichollOilsFuelModel;
import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.util.PriceUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class PostPriceFetcher extends AbstractPriceFetcher {

    public PostPriceFetcher(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public PriceResponse fetchPrice(final PriceRequest request) {
        String extractedText = "N/A";
        try {
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
        HttpHeaders headers = getHttpHeaders(request);

        if (request.getMediaType().equals(MediaType.APPLICATION_JSON)) {
            ObjectMapper objectMapper = new ObjectMapper();
            String postData = objectMapper.writeValueAsString(
                    new NichollOilsFuelModel(request.getNumberOfLitres()));
            return new HttpEntity<>(postData, headers);
        } else {
            return new HttpEntity<>(request.getPayload().getFormData(), headers);
        }
    }

    @NonNull
    private static HttpHeaders getHttpHeaders(final PriceRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(request.getMediaType());

        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        headers.set("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        headers.set("Accept-Language", "en-GB,en;q=0.9");

        headers.remove("Accept-Encoding");
        return headers;
    }

}
