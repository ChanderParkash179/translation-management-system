package com.tms.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class CustomUtils {

    public static String generateUsername(String fullName) {
        fullName = fullName.replace(" ", ".");
        return fullName.concat(UUID.randomUUID().toString().substring(0, 8));
    }

    public static <T> String writeAsJSON(T object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}