package com.tms.app.services.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.app.dtos.wrapper.PaginationResponse;

public interface RedisService {

    void saveData(String key, String value, int expiryInMinutes);

    String getData(String key);

    void deleteData(String key);

    <T> T getCachedData(String prefix, String value, String logMessage, Class<T> clazz);

    <T> PaginationResponse<T> getCachedData(String prefix, String logMessage, TypeReference<PaginationResponse<T>> clazz);

    void clearAllCache();
}
