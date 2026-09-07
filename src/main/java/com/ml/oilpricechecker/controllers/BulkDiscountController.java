package com.ml.oilpricechecker.controllers;

import com.ml.oilpricechecker.models.SupplierBulkDiscount;
import com.ml.oilpricechecker.service.BulkDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BulkDiscountController {

    private final BulkDiscountService bulkDiscountService;

    @Autowired
    public BulkDiscountController(final BulkDiscountService bulkDiscountService) {
        this.bulkDiscountService = bulkDiscountService;
    }

    @GetMapping("/bulk-discount")
    public List<SupplierBulkDiscount> getBulkDiscountCurve() throws Exception {
        return bulkDiscountService.getBulkDiscountCurve();
    }
}

