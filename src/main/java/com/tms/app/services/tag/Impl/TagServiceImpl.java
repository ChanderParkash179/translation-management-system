package com.tms.app.services.tag.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.app.dtos.tag.request.TagRequest;
import com.tms.app.dtos.tag.response.TagResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.entities.tag.Tag;
import com.tms.app.exceptions.BadRequestException;
import com.tms.app.repositories.tag.TagRepository;
import com.tms.app.services.redis.RedisService;
import com.tms.app.services.tag.TagService;
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
public class TagServiceImpl implements TagService {
    
    private final RedisService redisService;
    
    private final TagRepository tagRepository;

    @Override
    public TagResponse create(TagRequest request) {
        log.info("creating Tag");

        Tag tag = this.tagRepository.findByTagName(request.getTagName()).orElse(null);

        if (tag != null) {
            log.error("Tag is already available against given name");
            throw new BadRequestException("Tag is already available against given name");
        }

        log.info("creating new Tag and saving it and sending response");
        Tag response = this.tagRepository.save(Tag.builder()
                .tagName(request.getTagName())
                .tagDescription(request.getTagDescription())
                .isActive(true)
                .build());

        CompletableFuture.runAsync(() -> {
            String cacheKey = AppConstants.TAG_CACHE_FIND_CODE_PREFIX + request.getTagName();
            this.redisService.deleteData(cacheKey); // Assuming a delete method exists
            this.redisService.deleteData(AppConstants.TAG_CACHE_FIND_ALL_PREFIX); // Invalidate findAll cache
            try {
                this.redisService.saveData(AppConstants.TAG_CACHE_FIND_CODE_PREFIX + response.getTagName(), CustomUtils.writeAsJSON(response), 15);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return new TagResponse(response);
    }

    @Override
    public TagResponse update(TagRequest request) {
        log.info("updating Tag");

        Tag Tag = this.tagRepository.findByTagName(request.getTagName()).orElse(null);

        if (Tag != null) {
            log.error("Tag is already available against given name for update");
            throw new BadRequestException("Tag is already available against given name for update");
        }

        log.info("updating new Tag and saving it and sending response");
        Tag response = this.tagRepository.save(
                new Tag(
                        request.getTagName(),
                        request.getTagDescription()
                )
        );

        CompletableFuture.runAsync(() -> {
            String cacheKey = AppConstants.TAG_CACHE_FIND_CODE_PREFIX + request.getTagName();
            redisService.deleteData(cacheKey); // Assuming a delete method exists
            redisService.deleteData(AppConstants.TAG_CACHE_FIND_ALL_PREFIX); // Invalidate findAll cache
            try {
                this.redisService.saveData(AppConstants.TAG_CACHE_FIND_CODE_PREFIX + response.getTagName(), CustomUtils.writeAsJSON(response), 15);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return new TagResponse(response);
    }

    @Override
    public TagResponse findById(UUID id) {
        log.info("finding Tag by id");
        log.info("finding Tag by id: {}", id);

        Tag Tag = this.tagRepository.findById(id).orElseThrow(() -> {
            log.error("Tag not found by id");
            return new BadRequestException("Tag not found by id");
        });

        log.info("sending response of Tag by id");
        return new TagResponse(Tag);
    }

    @Override
    public TagResponse findByName(String name) {
        log.info("finding Tag by name");
        log.info("finding Tag by name: {}", name);

        log.info("sending response of Tag by name");
        return new TagResponse(this.findByTagCode(name));
    }

    @Override
    public PaginationResponse<TagResponse> findAll(Integer pageNo, Integer pageSize) {
        log.info("finding paginated Tags");
        log.info("finding paginated Tags request: pageNo: {}, pageSize: {}", pageNo, pageSize);

        String cacheKey = AppConstants.TAG_CACHE_FIND_ALL_PAGE_PREFIX + pageNo + AppConstants.FIND_ALL_SIZE_PREFIX + pageSize;
        PaginationResponse<TagResponse> cached = this.redisService.getCachedData(
                cacheKey,
                "Returning cached paginated Tags",
                new TypeReference<PaginationResponse<TagResponse>>() {}
        );
        if (cached != null) return cached;

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Tag> tagPage = this.tagRepository.findAll(pageable);

        log.info("Tag Page fetched Successfully");

        if (tagPage.getContent().isEmpty()) {
            log.info("No Tags found");
            return PaginationResponse.makeEmptyResponse();
        }

        log.info("Total Tags found: {}", tagPage.getContent().size());

        CompletableFuture.runAsync(() -> {
            try {
                redisService.saveData(AppConstants.TAG_CACHE_FIND_ALL_PAGE_PREFIX + pageNo + AppConstants.FIND_ALL_SIZE_PREFIX + pageSize, CustomUtils.writeAsJSON(tagPage), 10);
            } catch (JsonProcessingException e) {
                log.error("Failed to cache paginated Tags: {}", e.getMessage());
            }
        });

        return PaginationResponse.makeResponse(tagPage, TagResponse::new);
    }

    private Tag findByTagCode(String name) {
        log.info("validating caching");
        Tag cached = this.redisService.getCachedData(AppConstants.TAG_CACHE_FIND_CODE_PREFIX, name, "Returning cached Tag response for name", Tag.class);
        if (cached != null) return cached;

        log.info("no cached Tag data found, fetching from db");
        Tag response = this.tagRepository.findByTagName(name).orElseThrow(() -> {
            log.error("Tag not found by name");
            return new BadRequestException("Tag not found by name");
        });

        log.info("caching results");
        CompletableFuture.runAsync(() -> {
            try {
                this.redisService.saveData(AppConstants.TAG_CACHE_FIND_CODE_PREFIX + name, CustomUtils.writeAsJSON(response), 10);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("sending response of Tag by name call");
        return response;
    }
}