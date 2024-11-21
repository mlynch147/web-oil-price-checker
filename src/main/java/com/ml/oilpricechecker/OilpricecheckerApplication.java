package com.ml.oilpricechecker;

import com.ml.oilpricechecker.file.IFileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class OilpricecheckerApplication implements CommandLineRunner {

    @Autowired
    private IFileHandler fileHandler;

    public static void main(final String[] args) {
        SpringApplication.run(OilpricecheckerApplication.class, args);
    }

    // CommandLineRunner method will run when the application starts
    @Override
    public void run(final String... args) throws Exception {
        // Call the method from the injected FileHandler
        fileHandler.initializeFiles();
    }
}
