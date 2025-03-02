package com.tms.app.dtos.translation.request;

import lombok.*;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequest {

    @NotBlank(message = "Translation key is required")
    private String translationKey;

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Locale code is required")
    private String localeCode;

    private List<String> tags;

    @Override
    public String toString() {
        return "TranslationRequest{" +
                "translationKey='" + translationKey + '\'' +
                ", content='" + content + '\'' +
                ", localeCode='" + localeCode + '\'' +
                '}';
    }
}