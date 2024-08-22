package com.ml.oilpricechecker.controllers;

 import com.ml.oilpricechecker.models.Price;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.RestController;

 import java.util.List;
 import java.util.ArrayList;

 import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // This will map to src/main/resources/templates/index.html
    }

}

