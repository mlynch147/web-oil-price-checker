package com.ml.oilpricechecker.models.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.oilpricechecker.config.PriceRequestConfig;
import com.ml.oilpricechecker.enums.RequestType;
import com.ml.oilpricechecker.mappers.mappers.AmountOfLitresMapper;
import com.ml.oilpricechecker.models.Payload;
import com.ml.oilpricechecker.models.PriceRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PriceRequestBuilder {

    private static final String CONFIG_FILE = "src/main/resources/price_requests_config.json";
    private static final int ONE_THOUSAND = 1000;

    public List<PriceRequestConfig> priceRequestsConfig;

    public void initPriceRequestConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        priceRequestsConfig = objectMapper.readValue(
                new File(CONFIG_FILE), new TypeReference<>() { });
    }

    public List<PriceRequest> buildPriceRequests(final int numberOfLitres) throws Exception {
        List<PriceRequest> priceRequestList = new ArrayList<>();

        // Build PriceRequest objects from configurations
        for (PriceRequestConfig config : priceRequestsConfig) {
            String mappedLitres = AmountOfLitresMapper.mapAmountOfLitres(config.getAmountMapper(), numberOfLitres);

            Payload payload = null;
            if (config.getPayload() != null) {
                payload = new Payload();
                for (var entry : config.getPayload().entrySet()) {
                    String value = entry.getValue().replace("{numberOfLitres}", mappedLitres);
                    payload.add(entry.getKey(), value);
                }
            }

            Pattern pattern = Pattern.compile(
                    config.getPattern().replace("{numberOfLitres}", String.valueOf(numberOfLitres)));

            if (config.getAlternatePattern() != null && numberOfLitres == ONE_THOUSAND) {
                pattern = Pattern.compile(
                        config.getAlternatePattern().replace("{numberOfLitres}", String.valueOf(numberOfLitres)));
            }

            PriceRequest priceRequest = new PriceRequest(
                    config.getName(),
                    numberOfLitres,
                    config.getUrl(),
                    pattern,
                    RequestType.valueOf(config.getRequestType()),
                    payload,
                    config.getMediaType() != null ? MediaType.valueOf(config.getMediaType()) : null
            );

            priceRequestList.add(priceRequest);
        }

        return priceRequestList;
    }
}
