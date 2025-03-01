package com.tms.app.services.redis.Impl;

import com.tms.app.services.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveData(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}