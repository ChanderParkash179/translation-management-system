package com.tms.app.utils;

public class EndpointConstants {

    public static final String[] GET_ENDPOINTS = {

            "/api/v1/files",
            "/api/v1/document/**",
            "/api/v1/category",
            "/api/v1/page/active/**",
            "/api/v1/pathway/**",
            "/api/v1/tutor/**",
            "/api/v1/course/**",
            "/api/v1/courses-and-programs/**",
            "/api/v1/course/awesome/**",
            "/api/v1/course/popular-or-rated/**",
            "/api/v1/course/list/publish-status",
            "/api/v1/course/course-details",
            "/api/v1/plan/**",
            "/api/v1/profession/active",
            "/api/v1/programs-and-pathways",
            "/api/v1/search",
            "/api/v1/gallery",
            "/api/v1/gallery/public",
            "/api/v1/blog",
            "/api/v1/event",
            "/api/v1/program/popular/**",
            "/api/v1/page/all/**",
            "/api/v1/program/**",
            "/api/v1/certificate/**",
            "/api/v1/file/get/**",
            "/api/v1/certificate/fetch/**",
            "/api/v1/popular/profession",
            "/api/v1/vimeo/**",
            "/api/v1/article/fetch/**"
    };

    public static final String[] POST_ENDPOINTS = {

            "/api/v1/auth/login",
            "/api/v1/auth/signup",
            "/api/v1/auth/signup-tutor",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/forget-password",
            "/api/v1/auth/verify-otp",
            "/api/v1/auth/regenerate-otp",
            "/api/stripe/webhook",
            "/api/v1/users/contact-us",
            "/api/v1/vimeo/**",
            "/api/v1/users/accept"
    };

    public static final String[] PATCH_ENDPOINTS = {

            "/api/v1/auth/reset-password",
            "/api/v1/vimeo/**"
    };
}