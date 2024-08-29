package com.ml.oilpricechecker.fetcher;

import com.ml.oilpricechecker.models.PriceRequest;
import com.ml.oilpricechecker.models.PriceResponse;

public interface PriceFetcher {
    PriceResponse fetchPrice(PriceRequest request);
}
