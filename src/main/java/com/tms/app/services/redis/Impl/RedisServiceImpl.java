package com.tms.app.services.redis.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.app.services.redis.RedisService;
import com.tms.app.utils.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final AppLogger log = new AppLogger(RedisServiceImpl.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveData(String key, String value, int expiryInSeconds) {
        this.redisTemplate.opsForValue().set(key, value, expiryInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getData(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    @Override
    public <T> T getCachedData(String prefix, String value, String logMessage, Class<T> clazz) {
        String cacheKey = prefix + value;
        String cachedResponse = this.getData(cacheKey);

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
}