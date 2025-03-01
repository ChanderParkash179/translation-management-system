package com.tms.app.dtos.wrapper;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {

    private Integer status;
    private Boolean success;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    public static <T> APIResponse<T> success(Integer status, String message) {

        return new APIResponseBuilder<T>()
                .success(true)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> APIResponse<T> success(Integer status, String message, T data) {

        return new APIResponseBuilder<T>()
                .success(true)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }
}
