package com.example.klayswapalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KlaySwapAlertApplication {
    public static void main(String[] args) {
        SpringApplication.run(KlaySwapAlertApplication.class, args);
        KlaySwapService klaySwapService = new KlaySwapService();
        klaySwapService.crawl();

    }

}
