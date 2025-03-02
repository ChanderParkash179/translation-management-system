package com.tms.app.loaders;

import com.tms.app.factory.TranslationDataFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataPopulateLoader implements CommandLineRunner {

    private final TranslationDataFactory translationDataFactory;

    @Override
    public void run(String... args) throws Exception {
        // Run this via command line argument: --populate-data=100000
        if (Arrays.stream(args).anyMatch(arg -> arg.startsWith("--populate-data="))) {
            int count = Integer.parseInt(Arrays.stream(args)
                    .filter(arg -> arg.startsWith("--populate-data="))
                    .findFirst()
                    .get()
                    .split("=")[1]);
            this.translationDataFactory.generateTestData(count);
        }
    }
}