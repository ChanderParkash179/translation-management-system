package com.tms.app.dtos.wrapper;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer status;
    private Boolean success;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    public static <T> ApiResponse<T> success(Integer status, String message) {

        return new ApiResponseBuilder<T>()
                .success(true)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(Integer status, String message, T data) {

        return new ApiResponseBuilder<T>()
                .success(true)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }
}
