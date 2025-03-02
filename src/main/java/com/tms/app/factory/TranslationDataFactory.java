package com.tms.app.factory;

import com.tms.app.dtos.translation.request.TranslationRequest;
import com.tms.app.entities.locale.Locale;
import com.tms.app.repositories.locale.LocaleRepository;
import com.tms.app.services.translation.TranslationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class TranslationDataFactory {
    private final TranslationService translationService;
    private final LocaleRepository localeRepository;

    @Transactional
    public void generateTestData(int count) {
        List<String> locales = Arrays.asList("en", "fr", "es");
        List<String> tags = Arrays.asList("mobile", "desktop", "web");

        // Ensure locales exist
        locales.forEach(code -> {
            if (this.localeRepository.findByLocaleCode(code).isEmpty()) {
                this.localeRepository.save(new Locale(code, code.toUpperCase() ,"auto-language", false));
            }
        });

        // Generate translations
        IntStream.range(0, count).parallel().forEach(i -> {
            String key = "key_" + i;
            locales.forEach(localeCode -> {
                TranslationRequest request = TranslationRequest.builder()
                        .translationKey(key)
                        .content("Content " + i + " in " + localeCode)
                        .localeCode(localeCode)
                        .tags(tags.subList(0, new Random().nextInt(tags.size() + 1)))
                        .build();
                try {
                    this.translationService.create(request);
                } catch (Exception e) {
                    log.error("Failed to create test translation: {}", e.getMessage());
                }
            });
        });
    }
}