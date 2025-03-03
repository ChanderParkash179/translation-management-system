package com.tms.app.services.locale.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.app.dtos.locale.request.LocaleRequest;
import com.tms.app.dtos.locale.response.LocaleResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.entities.locale.Locale;
import com.tms.app.exceptions.BadRequestException;
import com.tms.app.repositories.locale.LocaleRepository;
import com.tms.app.services.locale.LocaleService;
import com.tms.app.services.redis.RedisService;
import com.tms.app.utils.AppConstants;
import com.tms.app.utils.CustomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocaleServiceImpl implements LocaleService {

    private final RedisService redisService;

    private final LocaleRepository localeRepository;

    @Override
    public LocaleResponse create(LocaleRequest request) {
        log.info("creating locale");
        log.info("creating locale request: {}", request);

        Locale locale = this.localeRepository.findByLocaleCode(request.getLocaleCode()).orElse(null);

        if (locale != null) {
            log.error("locale is already available against given code");
            throw new BadRequestException("locale is already available against given code");
        }

        log.info("creating new locale and saving it and sending response");
        Locale response = this.localeRepository.save(Locale.builder()
                .localeCode(request.getLocaleCode())
                .localeName(request.getLocaleName())
                .localeLanguage(request.getLocaleLanguage())
                .isDefault(request.getIsDefault())
                .isActive(true)
                .build());

        CompletableFuture.runAsync(() -> {
            String cacheKey = AppConstants.LOCALE_CACHE_FIND_CODE_PREFIX + request.getLocaleCode();
            this.redisService.deleteData(cacheKey); // Assuming a delete method exists
            this.redisService.deleteData(AppConstants.LOCALE_CACHE_FIND_ALL_PREFIX); // Invalidate findAll cache
            try {
                this.redisService.saveData(AppConstants.LOCALE_CACHE_FIND_CODE_PREFIX + response.getLocaleCode(), CustomUtils.writeAsJSON(response), 15);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return new LocaleResponse(response);
    }

    @Override
    public LocaleResponse update(LocaleRequest request) {
        log.info("updating locale");
        log.info("updating locale request: {}", request);

        Locale locale = this.localeRepository.findByLocaleCode(request.getLocaleCode()).orElse(null);

        if (locale != null) {
            log.error("locale is already available against given code for update");
            throw new BadRequestException("locale is already available against given code for update");
        }

        log.info("updating new locale and saving it and sending response");
        Locale response = this.localeRepository.save(
                new Locale(
                        request.getLocaleCode(),
                        request.getLocaleName(),
                        request.getLocaleLanguage(),
                        request.getIsDefault()
                )
        );

        CompletableFuture.runAsync(() -> {
            String cacheKey = AppConstants.LOCALE_CACHE_FIND_CODE_PREFIX + request.getLocaleCode();
            redisService.deleteData(cacheKey); // Assuming a delete method exists
            redisService.deleteData(AppConstants.LOCALE_CACHE_FIND_ALL_PREFIX); // Invalidate findAll cache
            try {
                this.redisService.saveData(AppConstants.LOCALE_CACHE_FIND_CODE_PREFIX + response.getLocaleCode(), CustomUtils.writeAsJSON(response), 15);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return new LocaleResponse(response);
    }

    @Override
    public LocaleResponse findById(UUID id) {
        log.info("finding locale by id");
        log.info("finding locale by id: {}", id);

        Locale locale = this.localeRepository.findById(id).orElseThrow(() -> {
            log.error("locale not found by id");
            return new BadRequestException("locale not found by id");
        });

        log.info("sending response of locale by id");
        return new LocaleResponse(locale);
    }

    @Override
    public LocaleResponse findByCode(String code) {
        log.info("finding locale by code");
        log.info("finding locale by code: {}", code);

        log.info("sending response of locale by code");
        return new LocaleResponse(this.findByLocaleCode(code));
    }

    @Override
    public PaginationResponse<LocaleResponse> findAll(Integer pageNo, Integer pageSize) {
        log.info("finding paginated locales");
        log.info("finding paginated locales request: pageNo: {}, pageSize: {}", pageNo, pageSize);

        String cacheKey = AppConstants.LOCALE_CACHE_FIND_ALL_PAGE_PREFIX + pageNo + AppConstants.FIND_ALL_SIZE_PREFIX + pageSize;
        PaginationResponse<LocaleResponse> cached = this.redisService.getCachedData(
                cacheKey,
                "Returning cached paginated locales",
                new TypeReference<PaginationResponse<LocaleResponse>>() {}
        );
        if (cached != null) return cached;
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Locale> localePage = this.localeRepository.findAll(pageable);

        log.info("Locale Page fetched Successfully");

        if (localePage.getContent().isEmpty()) {
            log.info("No Locales found");
            return PaginationResponse.makeEmptyResponse();
        }

        log.info("Total Locales found: {}", localePage.getContent().size());

        CompletableFuture.runAsync(() -> {
            try {
                redisService.saveData(AppConstants.LOCALE_CACHE_FIND_ALL_PAGE_PREFIX + pageNo + AppConstants.FIND_ALL_SIZE_PREFIX + pageSize, CustomUtils.writeAsJSON(localePage), 10);
            } catch (JsonProcessingException e) {
                log.error("Failed to cache paginated locales: {}", e.getMessage());
            }
        });

        return PaginationResponse.makeResponse(localePage, LocaleResponse::new);
    }

    private Locale findByLocaleCode(String code) {
        log.info("validating caching");
        Locale cached = this.redisService.getCachedData(AppConstants.LOCALE_CACHE_FIND_CODE_PREFIX, code, "Returning cached locale response for code", Locale.class);
        if (cached != null) return cached;

        log.info("no cached locale data found, fetching from db");
        Locale response = this.localeRepository.findByLocaleCode(code).orElseThrow(() -> {
            log.error("locale not found by code");
            return new BadRequestException("locale not found by code");
        });

        log.info("caching results");
        CompletableFuture.runAsync(() -> {
            try {
                this.redisService.saveData(AppConstants.LOCALE_CACHE_FIND_CODE_PREFIX + code, CustomUtils.writeAsJSON(response), 10);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("sending response of locale by code call");
        return response;
    }
}