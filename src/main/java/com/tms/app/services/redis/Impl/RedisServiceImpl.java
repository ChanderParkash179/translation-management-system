package com.tms.app.services.redis.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.services.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveData(String key, String value, int expiryInMinutes) {
        log.info("saving in cache");
        this.redisTemplate.opsForValue().set(key, value, expiryInMinutes, TimeUnit.MINUTES);
    }

    @Override
    public String getData(String key) {
        log.info("retrieving from cache");
        return this.redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteData(String key) {
        log.info("deleting from cache: {}", key);
        this.redisTemplate.delete(key);
    }

    @Override
    public <T> T getCachedData(String prefix, String value, String logMessage, Class<T> clazz) {
        String cacheKey = prefix + value;
        log.info("checking cache for {}", cacheKey);

        String cachedResponse = this.getData(cacheKey);
        log.info("validating cache response");

        if (cachedResponse != null) {
            log.info("{}: {}", logMessage, value);
            try {
                return objectMapper.readValue(cachedResponse, clazz);
            } catch (Exception e) {
                log.error("Failed to deserialize cached data for key {}: {}", cacheKey, e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> PaginationResponse<T> getCachedData(String key, String logMessage, TypeReference<PaginationResponse<T>> typeRef) {
        log.info("checking paginated cache for key_value : {}", key);

        String paginatedCachedResponse = this.getData(key);
        log.info("validating cache paginated response");

        if (paginatedCachedResponse != null) {
            log.info("{}: {}", logMessage, key);
            try {
                return objectMapper.readValue(paginatedCachedResponse, typeRef);
            } catch (Exception e) {
                log.error("Failed to deserialize paginated cached data for key {}: {}", key, e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public void clearAllCache() {
        log.info("clearing all cached data");
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushDb();
    }
}