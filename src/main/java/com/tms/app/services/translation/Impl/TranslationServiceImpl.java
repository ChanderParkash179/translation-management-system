package com.tms.app.services.translation.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.app.dtos.locale.response.LocaleResponse;
import com.tms.app.dtos.tag.response.TagResponse;
import com.tms.app.dtos.translation.request.TranslationRequest;
import com.tms.app.dtos.translation.response.TranslationResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.entities.locale.Locale;
import com.tms.app.entities.tag.Tag;
import com.tms.app.entities.translation.Translation;
import com.tms.app.entities.translationTag.TranslationTag;
import com.tms.app.enums.Message;
import com.tms.app.exceptions.BadRequestException;
import com.tms.app.repositories.locale.LocaleRepository;
import com.tms.app.repositories.tag.TagRepository;
import com.tms.app.repositories.translation.TranslationRepository;
import com.tms.app.repositories.translationTag.TranslationTagRepository;
import com.tms.app.services.redis.RedisService;
import com.tms.app.services.translation.TranslationService;
import com.tms.app.utils.AppConstants;
import com.tms.app.utils.CustomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    private final RedisService redisService;

    private final TagRepository tagRepository;
    private final LocaleRepository localeRepository;
    private final TranslationRepository translationRepository;
    private final TranslationTagRepository translationTagRepository;

    @Override
    public TranslationResponse create(TranslationRequest request) {
        Locale locale = this.localeRepository.findByLocaleCode(request.getLocaleCode())
                .orElseThrow(() -> new BadRequestException(Message.LOCALE_NOT_FOUND.getMessage()));

        Translation translation = Translation.builder()
                .translationKey(request.getTranslationKey())
                .content(request.getContent())
                .locale(locale)
                .isActive(true)
                .build();

        Translation savedTranslation = translationRepository.save(translation);

        // Handle tags if present
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            addTagsToTranslation(savedTranslation, request.getTags());
        }

        // Invalidate cache
        redisService.deleteData(AppConstants.TRANSLATION_CACHE_PREFIX + request.getTranslationKey() + ":" + request.getLocaleCode());
        redisService.deleteData(AppConstants.TRANSLATION_JSON_EXPORT_PREFIX + request.getLocaleCode());

        return this.mapToResponse(savedTranslation);
    }

    @Override
    public TranslationResponse get(String key, String localeCode) {
        String cacheKey = AppConstants.TRANSLATION_CACHE_PREFIX + key + ":" + localeCode;

        // Try cache first
        Translation cached = redisService.getCachedData(AppConstants.TRANSLATION_CACHE_PREFIX, key + ":" + localeCode, "Translation found in cache", Translation.class);
        if (cached != null) return this.mapToResponse(cached);

        Translation translation = translationRepository.findByKeyAndLocaleCode(key, localeCode)
                .orElseThrow(() -> new BadRequestException("Translation not found"));

        // Cache the result
        try {
            redisService.saveData(cacheKey, CustomUtils.writeAsJSON(translation), 30);
        } catch (JsonProcessingException e) {
            log.error("Failed to cache translation: {}", e.getMessage());
        }

        return this.mapToResponse(translation);
    }

    @Override
    public TranslationResponse update(UUID id, TranslationRequest request) {
        Translation translation = this.translationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Translation not found"));

        translation.setTranslationKey(request.getTranslationKey());
        translation.setContent(request.getContent());

        if (request.getLocaleCode() != null) {
            Locale locale = localeRepository.findByLocaleCode(request.getLocaleCode())
                    .orElseThrow(() -> new BadRequestException("Locale not found"));
            translation.setLocale(locale);
        }

        Translation updated = this.translationRepository.save(translation);

        // Update tags if provided
        if (request.getTags() != null) {
            updateTranslationTags(updated, request.getTags());
        }

        // Invalidate cache
        redisService.deleteData(AppConstants.TRANSLATION_CACHE_PREFIX + request.getTranslationKey() + ":" + request.getLocaleCode());
        redisService.deleteData(AppConstants.TRANSLATION_JSON_EXPORT_PREFIX + request.getLocaleCode());

        return this.mapToResponse(updated);
    }

    @Override
    public PaginationResponse<TranslationResponse> search(String key, List<String> tags, String localeCode, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Translation> translationPage;

        if (tags != null && !tags.isEmpty()) {
            translationPage = translationRepository.findByTagsAndLocaleCode(tags, localeCode, pageable);
        } else if (key != null) {
            translationPage = translationRepository.findByTranslationKeyContainingAndIsActiveTrue(key, pageable);
        } else {
            translationPage = translationRepository.findAllByLocaleCodePaginated(localeCode, pageable);
        }

        log.info("translation page fetched successfully");

        if (translationPage.getContent().isEmpty()) {
            log.info("No translations found");
            return PaginationResponse.makeEmptyResponse();
        }

        log.info("Total Tags found: {}", translationPage.getContent().size());
        List<TranslationResponse> responseList = translationPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PaginationResponse.<TranslationResponse>builder()
                .content(responseList)
                .pageNo(translationPage.getNumber())
                .pageSize(translationPage.getSize())
                .totalPages(translationPage.getTotalPages())
                .isFirstPage(translationPage.isFirst())
                .isLastPage(translationPage.isLast())
                .totalItems(translationPage.getTotalElements())
                .build();
    }

    @Override
    public Map<String, String> export(String localeCode) {
        String cacheKey = AppConstants.TRANSLATION_JSON_EXPORT_PREFIX + localeCode;

        // Try cache first
        try {
            String cachedJson = redisService.getData(cacheKey);
            if (cachedJson != null) {
                return objectMapper.readValue(cachedJson, new TypeReference<Map<String, String>>() {
                });
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse cached export: {}", e.getMessage());
        }

        List<Translation> translations = translationRepository.findAllByLocaleCode(localeCode);
        Map<String, String> translationMap = translations.stream()
                .collect(Collectors.toMap(
                        Translation::getTranslationKey,
                        Translation::getContent,
                        (v1, v2) -> v1 // In case of duplicate keys
                ));

        // Cache the result
        try {
            redisService.saveData(cacheKey, objectMapper.writeValueAsString(translationMap), 30);
        } catch (JsonProcessingException e) {
            log.error("Failed to cache export: {}", e.getMessage());
        }

        return translationMap;
    }

    private void addTagsToTranslation(Translation translation, List<String> tags) {
        tags.forEach(tagName -> {
            Tag tag = this.tagRepository.findByTagName(tagName)
                    .orElseGet(() -> this.tagRepository.save(Tag.builder()
                            .tagName(tagName)
                            .tagDescription("auto-created")
                            .isActive(true)
                            .build()));

            TranslationTag translationTag = TranslationTag.builder()
                    .translation(translation)
                    .tag(tag)
                    .isActive(true)
                    .build();
            this.translationTagRepository.save(translationTag);
        });
    }

    private void updateTranslationTags(Translation translation, List<String> tags) {
        this.translationTagRepository.deleteByTranslationId(translation.getId());
        addTagsToTranslation(translation, tags);
    }

    private TranslationResponse mapToResponse(Translation translation) {
        TranslationResponse response = this.modelMapper.map(translation, TranslationResponse.class);
        response.setId(translation.getId());
        response.setLocale(this.modelMapper.map(translation.getLocale(), LocaleResponse.class));
        List<TranslationTag> translationTags = translation.getTranslationTag();
        if (translationTags != null) {
            response.setTags(translationTags.stream()
                    .map(tt -> this.modelMapper.map(tt.getTag(), TagResponse.class))
                    .toList());
        }
        return response;
    }
}