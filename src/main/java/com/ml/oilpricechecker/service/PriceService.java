package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.enums.RequestType;
import com.ml.oilpricechecker.models.PriceRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.util.SSLUtilities;
import org.springframework.web.client.RestTemplate;

public class PriceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust pool size as needed

    public List<PriceResponse> makeConcurrentHttpCalls() throws Exception {

        SSLUtilities.disableSSLCertificateChecking();

        PriceRequest campsiePriceRequest = new PriceRequest(
                "Campsie Fuels",
                500,
                "https://campsiefuels.com/api/Quote/GetQuote" +
                        "?brandId=7&customerTypeId=1&productCode=k&postcode=BT474BN&quantity=" +
                        "{numberOfLitres}&maxSpend=0",
                "\"totalPriceIncVat\":(.*?),",
                RequestType.GET);

        PriceRequest scottsPriceRequest = new PriceRequest(
                "Scotts Fuels",
                500,
                "https://order.scottsfuels.com/api/Quote/GetQuote" +
                        "?brandId=1&customerTypeId=6&statedUse=1&productCode=K&postcode=BT474BN&quantity=" +
                        "{numberOfLitres}&maxSpend=0",
                "totalIncVat\":(.*?),",
                RequestType.GET);

        List<PriceRequest> priceRequestList = new ArrayList<>();
        priceRequestList.add(campsiePriceRequest);
        priceRequestList.add(scottsPriceRequest);

        List<CompletableFuture<PriceResponse>> futures = new ArrayList<>();

        for (PriceRequest priceRequest: priceRequestList) {
            CompletableFuture<PriceResponse> future = CompletableFuture.supplyAsync(() -> {
                String price = fetchData(priceRequest);
                return new PriceResponse(priceRequest.getSupplierName(), price);
            }, executorService);

            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
                .map(CompletableFuture::join) // Get the response data from each future
                .collect(Collectors.toList())
        ).get();
    }


    public String fetchData(PriceRequest request) {
        String url = request.getUrl();
        String htmlContent = restTemplate.getForObject(url, String.class);

        String extractedText = "N/A";
        Pattern pattern = Pattern.compile(request.getRegex());
        Matcher matcher = pattern.matcher(htmlContent);

        // Find the first match
        if (matcher.find()) {
            extractedText = matcher.group(1);
            if (!extractedText.contains(".")) {
                extractedText = extractedText + ".00";
            }
        }
        return extractedText;
    }
}


/*        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            Object payload = payloads.get(i);
            RequestType type = types.get(i);

            CompletableFuture<String> future;
            if (type == RequestType.GET) {
                future = CompletableFuture.supplyAsync(() -> fetchData(url), executorService);
            } else {
                future = CompletableFuture.supplyAsync(() -> sendData(url, payload), executorService);
            }
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
                .map(CompletableFuture::join) // Get the HTML response
                .collect(Collectors.toList())
        ).get(); // Block until all results are available*/
