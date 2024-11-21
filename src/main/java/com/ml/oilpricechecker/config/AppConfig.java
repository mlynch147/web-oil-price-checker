package com.ml.oilpricechecker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    private static final int MAX_THREADS = 10;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ExecutorService executorService() {
        // Define the thread pool size based on application needs
        return Executors.newFixedThreadPool(MAX_THREADS);
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.EU_WEST_2) // Replace with your desired region, e.g., US_EAST_1, EU_WEST_1
                .build();
    }
}

