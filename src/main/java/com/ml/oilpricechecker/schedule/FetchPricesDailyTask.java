package com.ml.oilpricechecker.schedule;

import com.ml.oilpricechecker.mappers.mappers.PriceMapper;
import com.ml.oilpricechecker.models.Price;
import com.ml.oilpricechecker.models.PriceResponse;
import com.ml.oilpricechecker.service.ChartService;
import com.ml.oilpricechecker.service.FileWriterService;
import com.ml.oilpricechecker.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class FetchPricesDailyTask {
    public static final int DEFAULT_NUMBER_OF_LITRES = 500;

    private final PriceService priceService;
    private final FileWriterService fileWriterService;

    // Constructor injection of the MyService dependency
    @Autowired
    public FetchPricesDailyTask(final PriceService priceService,
                                final FileWriterService fileWriterService) {
        this.priceService = priceService;
        this.fileWriterService = fileWriterService;
    }

    @Scheduled(cron = "0 6 13 * * ?")
    public void executeTask() throws Exception {
        System.out.println("Running scheduled task at 2 AM");

        List<Price> data = new ArrayList<>();

        List<PriceResponse> pricesResponses =
                priceService.getCurrentPrices(DEFAULT_NUMBER_OF_LITRES);

        for (PriceResponse priceResponse: pricesResponses) {
            data.add(PriceMapper.mapPriceResponseToPrice(priceResponse));
        }

        CompletableFuture.runAsync(() -> fileWriterService.writePricesToFile(data));
    }
}
