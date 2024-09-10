package com.ml.oilpricechecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OilpricecheckerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(OilpricecheckerApplication.class, args);
    }
}
