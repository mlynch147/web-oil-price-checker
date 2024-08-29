package com.ml.oilpricechecker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ml.oilpricechecker.enums.RequestType;
import com.ml.oilpricechecker.mappers.mappers.CraigFuelsAmountMapper;
import com.ml.oilpricechecker.models.NichollOilsFuelModel;
import com.ml.oilpricechecker.models.Payload;
import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.util.SSLUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class PriceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust pool size as needed

    public List<PriceResponse> makeConcurrentHttpCalls(int numberOfLitres) throws Exception {

        SSLUtilities.disableSSLCertificateChecking();

        Payload craigsPayload = new Payload();
        craigsPayload.add("county", "4");
        craigsPayload.add("required_quantity", CraigFuelsAmountMapper.MapAmountToValue("500"));

        PriceRequest craigsFuelsPriceRequest = new PriceRequest(
                "Craigs Fuels",
                numberOfLitres,
                "https://www.craigfuels.com/purchase",
                Pattern.compile("£(.*?)<"),
                RequestType.POST,
                craigsPayload,
                MediaType.APPLICATION_FORM_URLENCODED);

        PriceRequest mcginleysPriceRequest = new PriceRequest(
                "McGinleys Oils",
                numberOfLitres,
                "https://mcginleysoil.com/",
                Pattern.compile("<strong>" + Pattern.quote("500L") + "</strong>\\s*<p>&pound;([^<]+)</p>"),
                RequestType.GET);

        Pattern mooresPattern;
        String startingPattern = numberOfLitres + "\":";
        if (numberOfLitres != 1000) {
            mooresPattern = Pattern.compile(Pattern.quote(startingPattern) + "(.*?),");
        } else {
            mooresPattern = Pattern.compile(Pattern.quote(startingPattern) + "(.*?)\\}");
        }

        Payload mooresPayload = new Payload();
        mooresPayload.add("action", "delivery_cost");
        mooresPayload.add("disPostcode", "BT47");
        mooresPayload.add("productId", "4790");

        PriceRequest mooresPriceRequest = new PriceRequest(
                "Moores Fuels",
                numberOfLitres,
                "https://www.mooresfuels.com/wp-admin/admin-ajax.php",
                mooresPattern,
                RequestType.POST,
                mooresPayload,
                MediaType.APPLICATION_FORM_URLENCODED);

        PriceRequest springtownPriceRequest = new PriceRequest(
                "Springtown Fuels",
                numberOfLitres,
        "https://order.springtownfuels.com/api/Quote/GetQuote" +
                "?brandId=1&customerTypeId=1&statedUse=1&productCode=K&postcode=BT474BN&quantity=" +
                "{numberOfLitres}&maxSpend=0",
                Pattern.compile("totalIncVat\":(.*?),"),
        RequestType.GET);

        PriceRequest campsiePriceRequest = new PriceRequest(
                "Campsie Fuels",
                numberOfLitres,
                "https://campsiefuels.com/api/Quote/GetQuote" +
                        "?brandId=7&customerTypeId=1&productCode=k&postcode=BT474BN&quantity=" +
                        "{numberOfLitres}&maxSpend=0",
                Pattern.compile("\"totalPriceIncVat\":(.*?),"),
                RequestType.GET);

        PriceRequest scottsPriceRequest = new PriceRequest(
                "Scotts Fuels",
                numberOfLitres,
                "https://order.scottsfuels.com/api/Quote/GetQuote" +
                        "?brandId=1&customerTypeId=6&statedUse=1&productCode=K&postcode=BT474BN&quantity=" +
                        "{numberOfLitres}&maxSpend=0",
                Pattern.compile("totalIncVat\":(.*?),"),
                RequestType.GET);

        PriceRequest nichollsOilPriceRequest = new PriceRequest(
                "Nicholls Oils",
                numberOfLitres,
                "https://nicholloils.fuelsoft.co.uk/WEBPLUS/fuelsoftapi/383cea92-b212-4fff-890c-8826ba380ba1?url=Quotes/A01",
                Pattern.compile("\"TotalGoods\":(.*?),"),
                RequestType.POST,
                null,
                MediaType.APPLICATION_JSON);

        List<PriceRequest> priceRequestList = new ArrayList<>();
        priceRequestList.add(craigsFuelsPriceRequest);
        priceRequestList.add(mooresPriceRequest);
        priceRequestList.add(mcginleysPriceRequest);
        priceRequestList.add(campsiePriceRequest);
        priceRequestList.add(scottsPriceRequest);
        priceRequestList.add(springtownPriceRequest);
        priceRequestList.add(nichollsOilPriceRequest);

        List<CompletableFuture<PriceResponse>> futures = new ArrayList<>();

        for (PriceRequest priceRequest: priceRequestList) {

            CompletableFuture<PriceResponse> future;

            if (priceRequest.getRequestType() == RequestType.GET) {
                future = fetchPriceAsync(priceRequest, this::fetchDataWithGet);
            } else {
                future = fetchPriceAsync(priceRequest, this::fetchDataWithPost);
            }
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
                .map(CompletableFuture::join) // Get the response data from each future
                .collect(Collectors.toList())
        ).get();
    }


    public String fetchDataWithGet(PriceRequest request) {
        String url = request.getUrl();
        String htmlContent = restTemplate.getForObject(url, String.class);

        String extractedText = "N/A";
        if (htmlContent != null) {
            Matcher matcher = request.getPattern().matcher(htmlContent);

            // Find the first match
            if (matcher.find()) {
                extractedText = "£" + matcher.group(1);
                if (!extractedText.contains(".")) {
                    extractedText = extractedText + ".00";
                }
            }
        }
        return extractedText;
    }

    public String fetchDataWithPost(PriceRequest request) {
        String extractedText = "N/A";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(request.getMediaType());

            HttpEntity<Object> requestEntity;
            if (request.getMediaType().equals(MediaType.APPLICATION_JSON)) {
                ObjectMapper objectMapper = new ObjectMapper();
                String postData = objectMapper.writeValueAsString(new NichollOilsFuelModel("500"));
                requestEntity = new HttpEntity<>(postData, headers);
            } else {
                requestEntity = new HttpEntity<>(request.getPayload().getFormData(), headers);
            }
            String htmlContent = restTemplate.postForObject(request.getUrl(), requestEntity, String.class);

            if (htmlContent != null) {
                Matcher matcher = request.getPattern().matcher(htmlContent);
                if (matcher.find()) {
                    extractedText = "£" + matcher.group(1);
                    if (!extractedText.contains(".")) {
                        extractedText = extractedText + ".00";
                    }
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            extractedText = "HTTP Exception: " + e.getStatusCode();
        } catch (Exception e) {
            // Log any other exceptions that occur
            extractedText = "Exception occurred: " + e.getMessage();
        }
        return extractedText;
    }

    private CompletableFuture<PriceResponse> fetchPriceAsync(
            PriceRequest priceRequest, Function<PriceRequest, String> fetchMethod) {
        return CompletableFuture.supplyAsync(() -> {
            String price = fetchMethod.apply(priceRequest);
            return new PriceResponse(
                    priceRequest.getSupplierName(),
                    price,
                    priceRequest.getNumberOfLitres());
        }, executorService);
    }
}
