package com.tms.app.dtos.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private Boolean success;
    private String message;
    private String devMessage;
    private String path;
    private String method;
    private LocalDateTime timestamp;
    private Map<String, Object> errors;

    public static ErrorResponse error(int status, String message) {
        return new ErrorResponseBuilder()
                .status(status)
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse error(int status, String message, Map<String, Object> errors) {
        return new ErrorResponseBuilder()
                .status(status)
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
