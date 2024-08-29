package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.enums.RequestType;

import com.ml.oilpricechecker.fetcher.GetPriceFetcher;
import com.ml.oilpricechecker.fetcher.PostPriceFetcher;
import com.ml.oilpricechecker.mappers.mappers.CraigFuelsAmountMapper;
import com.ml.oilpricechecker.models.Payload;
import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.util.SSLUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PriceService  {

    public static final int ONE_THOUSAND = 1000;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService;

    @Autowired
    public PriceService(final RestTemplate restTemplate, final ExecutorService executorService) {
        this.restTemplate = restTemplate;
        this.executorService = executorService;
    }

    public List<PriceResponse> makeConcurrentHttpCalls(final int numberOfLitres) throws Exception {
        SSLUtilities.disableSSLCertificateChecking();

        List<PriceRequest> priceRequestList = buildPriceRequests(numberOfLitres);
        List<CompletableFuture<PriceResponse>> futures = priceRequestList.stream()
                .map(this::fetchPriceAsync)
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .get();
    }

    private List<PriceRequest> buildPriceRequests(final int numberOfLitres) {
        Payload craigsPayload = new Payload();
        craigsPayload.add("county", "4");
        craigsPayload.add("required_quantity", CraigFuelsAmountMapper.mapAmountToValue("500"));

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
        if (numberOfLitres != ONE_THOUSAND) {
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
                "https://order.springtownfuels.com/api/Quote/GetQuote"
                        + "?brandId=1&customerTypeId=1&statedUse=1&productCode=K&postcode=BT474BN&quantity="
                        + "{numberOfLitres}&maxSpend=0",
                Pattern.compile("totalIncVat\":(.*?),"),
                RequestType.GET);

        PriceRequest campsiePriceRequest = new PriceRequest(
                "Campsie Fuels",
                numberOfLitres,
                "https://campsiefuels.com/api/Quote/GetQuote"
                        + "?brandId=7&customerTypeId=1&productCode=k&postcode=BT474BN&quantity="
                        + "{numberOfLitres}&maxSpend=0",
                Pattern.compile("\"totalPriceIncVat\":(.*?),"),
                RequestType.GET);

        PriceRequest scottsPriceRequest = new PriceRequest(
                "Scotts Fuels",
                numberOfLitres,
                "https://order.scottsfuels.com/api/Quote/GetQuote"
                        + "?brandId=1&customerTypeId=6&statedUse=1&productCode=K&postcode=BT474BN&quantity="
                        + "{numberOfLitres}&maxSpend=0",
                Pattern.compile("totalIncVat\":(.*?),"),
                RequestType.GET);

        PriceRequest nichollsOilPriceRequest = new PriceRequest(
                "Nicholls Oils",
                numberOfLitres,
                "https://nicholloils.fuelsoft.co.uk/WEBPLUS/"
                        + "fuelsoftapi/383cea92-b212-4fff-890c-8826ba380ba1?url=Quotes/A01",
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

        return priceRequestList;

    }

    private CompletableFuture<PriceResponse> fetchPriceAsync(final PriceRequest request) {
        com.ml.oilpricechecker.fetcher.PriceFetcher fetcher = request.getRequestType() == RequestType.GET
                ? new GetPriceFetcher(restTemplate) : new PostPriceFetcher(restTemplate);

        return CompletableFuture.supplyAsync(() -> fetcher.fetchPrice(request), executorService);
    }
}
