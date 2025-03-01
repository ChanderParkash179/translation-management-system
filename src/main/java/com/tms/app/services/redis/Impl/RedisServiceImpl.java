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
    public <T> T getCachedData(String prefix, String value, String logMessage, Class<T> clazz) {
        String cacheKey = prefix + value;
        log.info("checking cache for {}", cacheKey);

        String cachedResponse = this.getData(cacheKey);
        log.info("cache response {}", cachedResponse);

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