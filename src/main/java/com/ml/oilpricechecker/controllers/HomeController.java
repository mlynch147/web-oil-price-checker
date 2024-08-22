package com.ml.oilpricechecker.controllers;

 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.GetMapping;

 @Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // This will map to src/main/resources/templates/index.html
    }

     @GetMapping("/index2")
     public String home2() {
         return "index2"; // This will map to src/main/resources/templates/index.html
     }
}

