package com.ml.oilpricechecker.models.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.oilpricechecker.config.SupplierConfig;
import com.ml.oilpricechecker.constants.Constants;
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

    public List<SupplierConfig> supplierConfigList;

    public void loadSupplierConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        supplierConfigList = objectMapper.readValue(
                new File(CONFIG_FILE), new TypeReference<>() { });

        for (SupplierConfig supplierConfig : supplierConfigList) {
            String displayName = supplierConfig.getDisplayName();
            String fileName = supplierConfig.getFileName();

            if (supplierConfig.getDisplayName().contains("Craigs")
                    || supplierConfig.getDisplayName().contains("Campsie")
                    || supplierConfig.getDisplayName().contains("Scotts")) {
                Constants.fourteenDaysFileNameMap.put(fileName, displayName);
                Constants.fourteenDaysDisplayNameMap.put(displayName, fileName);

                if (supplierConfig.getDisplayName().contains("Craigs")) {
                    Constants.CRAIGS_DISPLAY_NAME = supplierConfig.getDisplayName();
                }
                if (supplierConfig.getDisplayName().contains("Campsie")) {
                    Constants.CAMPSIE_DISPLAY_NAME = supplierConfig.getDisplayName();
                }
                if (supplierConfig.getDisplayName().contains("Scotts")) {
                    Constants.SCOTTS_DISPLAY_NAME = supplierConfig.getDisplayName();
                }
            }

            Constants.weeklyFileNameMap.put("weekly_comparison_" + fileName, displayName);
            Constants.weeklyDisplayNameMap.put(displayName, "weekly_comparison_" + fileName);

            Constants.sixMonthsFileNameMap.put("six_months_" + fileName, displayName);
            Constants.sixMonthsDisplayNameMap.put(displayName, "six_months_" + fileName);
        }
    }

    public List<PriceRequest> buildPriceRequests(final int numberOfLitres) throws Exception {
        List<PriceRequest> priceRequestList = new ArrayList<>();

        // Build PriceRequest objects from configurations
        for (SupplierConfig config : supplierConfigList) {
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
                    config.getDisplayName(),
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
