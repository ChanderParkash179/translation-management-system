package com.tms.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TranslationManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TranslationManagementSystemApplication.class, args);
    }
}