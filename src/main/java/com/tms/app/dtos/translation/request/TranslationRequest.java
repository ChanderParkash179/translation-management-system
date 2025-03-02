package com.tms.app.dtos.translation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating translation details")
public class TranslationRequest {

    @Schema(description = "Key identifying the translation", example = "welcome.message")
    @NotBlank(message = "Translation key is required")
    private String translationKey;

    @Schema(description = "Translated content", example = "Welcome to our application!")
    @NotBlank(message = "Content is required")
    private String content;

    @Schema(description = "Locale code associated with the translation", example = "en_US")
    @NotBlank(message = "Locale code is required")
    private String localeCode;

    @Schema(description = "List of tag names associated with the translation", example = "[\"greeting\", \"message\"]")
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