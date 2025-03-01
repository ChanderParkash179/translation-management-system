package com.tms.app.services.redis;

public interface RedisService {

    void saveData(String key, String value, int expiryInMinutes);

    String getData(String key);

    <T> T getCachedData(String prefix, String value, String logMessage, Class<T> clazz);
}
