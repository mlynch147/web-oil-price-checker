package com.ml.oilpricechecker.service;

import com.ml.oilpricechecker.enums.RequestType;

import com.ml.oilpricechecker.fetcher.GetPriceFetcher;
import com.ml.oilpricechecker.fetcher.PostPriceFetcher;
import com.ml.oilpricechecker.fetcher.PriceFetcher;
import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.models.builders.PriceRequestBuilder;
import com.ml.oilpricechecker.util.SSLUtilities;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PriceService  {

    private final RestTemplate restTemplate;
    private final ExecutorService executorService;
    private final PriceRequestBuilder priceRequestBuilder;

    @Autowired
    public PriceService(final RestTemplate restTemplate,
                        final ExecutorService executorService,
                        final PriceRequestBuilder priceRequestBuilder) {
        this.restTemplate = restTemplate;
        this.executorService = executorService;
        this.priceRequestBuilder = priceRequestBuilder;
    }

    public List<PriceResponse> getCurrentPrices(final int numberOfLitres) throws Exception {
        SSLUtilities.disableSSLCertificateChecking();

        List<PriceRequest> priceRequestList = priceRequestBuilder.buildPriceRequests(numberOfLitres);

        List<CompletableFuture<PriceResponse>> futures = priceRequestList.stream()
                .map(this::fetchPriceAsync)
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                        .get();
    }


    private CompletableFuture<PriceResponse> fetchPriceAsync(final PriceRequest request) {
        PriceFetcher fetcher = request.getRequestType() == RequestType.GET
                ? new GetPriceFetcher(restTemplate) : new PostPriceFetcher(restTemplate);

        return CompletableFuture.supplyAsync(() -> fetcher.fetchPrice(request), executorService);
    }
}
