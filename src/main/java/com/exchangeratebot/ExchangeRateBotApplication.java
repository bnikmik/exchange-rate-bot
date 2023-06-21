package com.exchangeratebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ExchangeRateBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRateBotApplication.class, args);
    }

}
