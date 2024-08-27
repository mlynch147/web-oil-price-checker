package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.enums.RequestType;
import com.ml.oilpricechecker.mappers.mappers.CraigFuelsAmountMapper;
import com.ml.oilpricechecker.models.Payload;
import com.ml.oilpricechecker.models.PriceRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.util.SSLUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PriceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust pool size as needed

    public List<PriceResponse> makeConcurrentHttpCalls() throws Exception {

        SSLUtilities.disableSSLCertificateChecking();

        Payload craigsPayload = new Payload();
        craigsPayload.add("county", "4");
        craigsPayload.add("required_quantity", CraigFuelsAmountMapper.MapAmountToValue("500"));

        PriceRequest craigsFuelsPriceRequest = new PriceRequest(
                "Craigs Fuels",
                500,
                "https://www.craigfuels.com/purchase",
                Pattern.compile("£(.*?)<"),
                RequestType.POST,
                craigsPayload);

        PriceRequest mcginleysPriceRequest = new PriceRequest(
                "McGinleys Oils",
                500,
                "https://mcginleysoil.com/",
                Pattern.compile("<strong>" + Pattern.quote("500L") + "</strong>\\s*<p>&pound;([^<]+)</p>"),
                RequestType.GET);



        Pattern mooresPattern;
        String startingPattern = "500" + "\":";
        if (Integer.parseInt("500") != 1000) {
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
                500,
                "https://www.mooresfuels.com/wp-admin/admin-ajax.php",
                mooresPattern,
                RequestType.POST,
                mooresPayload);

        PriceRequest springtownPriceRequest = new PriceRequest(
                "Springtown Fuels",
                500,
        "https://order.springtownfuels.com/api/Quote/GetQuote" +
                "?brandId=1&customerTypeId=1&statedUse=1&productCode=K&postcode=BT474BN&quantity=" +
                "{numberOfLitres}&maxSpend=0",
                Pattern.compile("totalIncVat\":(.*?),"),
        RequestType.GET);

        PriceRequest campsiePriceRequest = new PriceRequest(
                "Campsie Fuels",
                500,
                "https://campsiefuels.com/api/Quote/GetQuote" +
                        "?brandId=7&customerTypeId=1&productCode=k&postcode=BT474BN&quantity=" +
                        "{numberOfLitres}&maxSpend=0",
                Pattern.compile("\"totalPriceIncVat\":(.*?),"),
                RequestType.GET);

        PriceRequest scottsPriceRequest = new PriceRequest(
                "Scotts Fuels",
                500,
                "https://order.scottsfuels.com/api/Quote/GetQuote" +
                        "?brandId=1&customerTypeId=6&statedUse=1&productCode=K&postcode=BT474BN&quantity=" +
                        "{numberOfLitres}&maxSpend=0",
                Pattern.compile("totalIncVat\":(.*?),"),
                RequestType.GET);




//        String DeliveryOptionWeighting = "Standard Delivery";
//        String OrderQty;
//        String Postcode = "BT47 4HR";
//        String CountryCode = "UK";
//        String ProductCode = "0002";
//        String DeliveryScheduleWeighting = "Web 5 Day";

        Payload nichollsOilPayload = new Payload();
        mooresPayload.add("DeliveryOptionWeighting", "Standard Delivery");
        mooresPayload.add("OrderQty", "500");
        mooresPayload.add("Postcode", "BT47 4HR");
        mooresPayload.add("CountryCode", "UK");
        mooresPayload.add("ProductCode", "0002");
        mooresPayload.add("DeliveryScheduleWeighting", "Web 5 Day");

        PriceRequest nichollsOilPriceRequest = new PriceRequest(
                "Nicholls Oils",
                500,
                "https://nicholloils.fuelsoft.co.uk/WEBPLUS/fuelsoftapi/383cea92-b212-4fff-890c-8826ba380ba1?url=Quotes/A01",
                Pattern.compile("\"TotalGoods\":(.*?),"),
                RequestType.POST,
                nichollsOilPayload);



        List<PriceRequest> priceRequestList = new ArrayList<>();
        priceRequestList.add(craigsFuelsPriceRequest);
        priceRequestList.add(mooresPriceRequest);
        priceRequestList.add(mcginleysPriceRequest);
        priceRequestList.add(campsiePriceRequest);
        priceRequestList.add(scottsPriceRequest);
        priceRequestList.add(springtownPriceRequest);
  //      priceRequestList.add(nichollsOilPriceRequest);

        List<CompletableFuture<PriceResponse>> futures = new ArrayList<>();

        for (PriceRequest priceRequest: priceRequestList) {

            CompletableFuture<PriceResponse> future;

            if (priceRequest.getRequestType() == RequestType.GET) {
                future = fetchPriceAsync(priceRequest, this::fetchDataWithGet);
/*
                future = CompletableFuture.supplyAsync(() -> {
                            String price = fetchDataWithGet(priceRequest);
                            return new PriceResponse(
                                    priceRequest.getSupplierName(),
                                    price,
                                    priceRequest.getNumberOfLitres());
                        }, executorService);*/
            } else {
                //to do - new method for post...
                future = fetchPriceAsync(priceRequest, this::fetchDataWithPost);
/*
                future = CompletableFuture.supplyAsync(() -> {
                    String price = fetchDataWithPost(priceRequest);
                    return new PriceResponse(
                            priceRequest.getSupplierName(),
                            price,
                            priceRequest.getNumberOfLitres());
                }, executorService);*/
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
        Matcher matcher = request.getPattern().matcher(htmlContent);

        // Find the first match
        if (matcher.find()) {
            extractedText = "£" + matcher.group(1);
            if (!extractedText.contains(".")) {
                extractedText = extractedText + ".00";
            }
        }
        return extractedText;
    }

    /**
     * NOT TESTED YET
     *
     * @param request
     * @return
     */
    public String fetchDataWithPost(PriceRequest request) {

        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        // Create an HttpEntity with the form data and headers
        // HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Use RestTemplate to send the POST request




        /////////////
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        // Wrapping the payload in an HttpEntity with headers
        //HttpEntity<Object> requestEntity = new HttpEntity<>(request.getPayload(), headers);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(request.getPayload().getFormData(), headers);


        // Sending the POST request and getting the response
        String htmlContent = restTemplate.postForObject(request.getUrl(), requestEntity, String.class);

        String extractedText = "N/A";
        Matcher matcher = request.getPattern().matcher(htmlContent);

        // Find the first match
        if (matcher.find()) {
            extractedText = "£" + matcher.group(1);
            if (!extractedText.contains(".")) {
                extractedText = extractedText + ".00";
            }
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
