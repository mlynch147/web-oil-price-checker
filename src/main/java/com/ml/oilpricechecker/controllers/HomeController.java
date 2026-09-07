package com.ml.oilpricechecker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "dashboard"; // This will map to src/main/resources/templates/dashboard.html
    }

    @GetMapping("/v1")
    public String legacyHome() {
        return "index"; // Legacy UI: src/main/resources/templates/index.html
    }

}

