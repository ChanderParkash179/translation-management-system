package com.tms.app.services.redis;

public interface RedisService {

    void saveData(String key, String value);

    String getData(String key);
}
