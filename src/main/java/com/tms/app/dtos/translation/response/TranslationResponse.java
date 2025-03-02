package com.tms.app.dtos.translation.response;

import com.tms.app.dtos.locale.response.LocaleResponse;
import com.tms.app.dtos.tag.response.TagResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing translation details")
public class TranslationResponse {

    @Schema(description = "Unique identifier of the translation", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private UUID id;

    @Schema(description = "Key identifying the translation", example = "welcome.message")
    private String translationKey;

    @Schema(description = "Translated content", example = "Welcome to our application!")
    private String content;

    @Schema(description = "Locale associated with the translation")
    private LocaleResponse locale;

    @Schema(description = "List of tags associated with the translation")
    private List<TagResponse> tags;

    @Schema(description = "Indicates if the translation is currently active", example = "true")
    private Boolean isActive;
}