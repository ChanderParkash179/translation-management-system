package com.tms.app.utils;

public class EndpointConstants {

    public static final String[] GET_ENDPOINTS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "swagger-ui.html"
    };

    public static final String[] POST_ENDPOINTS = {
            "/api/v1/auth/login",
            "/api/v1/auth/signup"
    };

    public static final String[] PATCH_ENDPOINTS = {};
}