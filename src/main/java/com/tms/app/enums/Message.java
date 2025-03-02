package com.tms.app.enums;

import lombok.Getter;

@Getter
public enum Message {

    VALIDATION_ERROR("Validation Error"),
    RESOURCE_NOT_FOUND("Resource Not Found"),
    DUPLICATE_EMAIL("User Email Already Exists"),
    DUPLICATE_CATEGORY("Category Already Exists"),
    DUPLICATE_COURSE_TUTOR("Course Already Exists with given Tutor"),
    DUPLICATE_PROGRAM("Program Already Exists"),
    DUPLICATE_SECTION_COURSE("Section Already Exists with given Course"),
    INVALID_CREDENTIALS("Invalid Email or Password"),
    DUPLICATE_ROLE("Role Already Exists"),
    AUTHENTICATION_REQUIRED("Authentication Required"),
    SESSION_EXPIRED("Session Expired"),
    NOT_AUTHORIZED("Not Authorized"),
    ACCESS_DENIED("Access Denied"),
    LINK_EXPIRED("Link has been Expired"),
    INTERNAL_SERVER_ERROR("Unexpected Error. Please try again"),
    LOGIN_SUCCESS("User Logged In Successfully"),
    UNPROCESSABLE_ENTITY("The provided password is compromised and cannot be used"),
    SIGNUP_SUCCESS("User Registered Successfully"),

    // LOCALE
    LOCALE_CREATED("locale created successfully"),
    LOCALE_UPDATED("locale updated successfully"),
    LOCALE_REMOVED("locale removed successfully"),
    LOCALE_FOUNDED_BY_ID("locale founded by id successfully"),
    LOCALE_FOUNDED_BY_CODE("locale founded by code successfully"),
    LOCALE_NOT_FOUND("locale not found"),
    LOCALE_LISTED("locale listed successfully");
    private final String message;

    Message(String message) {

        this.message = message;
    }
}
