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
    DUPLICATE_USERNAME_EMAIL("Username OR Email Already Exists"),
    DUPLICATE_PROGRAM_ENROLLMENT("You're Already Enrolled in Program: "),
    DUPLICATE_COURSE_ENROLLMENT("You're Already Enrolled in Course: "),
    UNPROCESSABLE_ENTITY("The provided password is compromised and cannot be used"),
    DUPLICATE_PROFESSION("Profession Already Exists"),
    DUPLICATE_SUBJECT("Subject Already Exists"),
    QUIZ_NOT_FOUND("Quiz not found with id: "),
    QUESTION_NOT_FOUND("Question not found with id: "),
    QUESTION_OPTION_NOT_FOUND("Question option not found with id: "),
    COURSE_NOT_FOUND("Course not found with id: "),
    REMINDER_NOT_FOUND("Reminder not found with id: "),
    CONTENT_NOT_FOUND("Content not found with id: "),
    DOCUMENT_NOT_FOUND("Document not found with id: "),
    COMMENT_NOT_FOUND("Comment not found with id: "),
    RUBRIC_NOT_FOUND("Rubric not found with id: "),
    SURVEY_NOT_FOUND("Survey not found with course id: "),
    ARTICLE_NOT_FOUND("Article not found with id: "),
    ARTICLE_CONTENT_NOT_FOUND("Article content not found with id: ");

    private final String message;

    Message(String message) {

        this.message = message;
    }
}
