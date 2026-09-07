package com.ml.oilpricechecker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Alias route for the dashboard UI, which is also served from "/".
 * Kept so existing "/dashboard" links continue to work.
 */
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // maps to src/main/resources/templates/dashboard.html
    }

}

