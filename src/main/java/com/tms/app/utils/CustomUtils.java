package com.tms.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.UUID;

public class CustomUtils {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Serialize as ISO-8601 string
    }

    public static <T> String writeAsJSON(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static String generateUsername(String fullName) {
        fullName = fullName.replace(" ", ".");
        return fullName.concat(UUID.randomUUID().toString().substring(0, 8));
    }
}