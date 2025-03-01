package com.tms.app.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CustomUtils {

    public static List<String> generateUserFirstNameAndLastName(String fullName) {
        fullName = fullName.replace(" ", "");
        String[] parts = fullName.split("(?=[A-Z])");
        return Arrays.asList(parts);
    }

    public static String generateSerialKey() {
        SecureRandom random = new SecureRandom();
        BigInteger serialKey = new BigInteger(64, random);
        return serialKey.toString(14).toUpperCase();  // Return as hex string
    }

    public static String generateUsername(String fullName) {
        fullName = fullName.replace(" ", ".");
        return fullName.concat(UUID.randomUUID().toString().substring(0, 8));
    }
}