package com.tms.app.dtos.locale.response;

import com.tms.app.entities.locale.Locale;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing locale details")
public class LocaleResponse {

    @Schema(description = "Unique identifier of the locale", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private UUID id;

    @Schema(description = "Code representing the locale", example = "en_US")
    private String localeCode;

    @Schema(description = "Human-readable name of the locale", example = "English (United States)")
    private String localeName;

    @Schema(description = "Language associated with the locale", example = "English")
    private String localeLanguage;

    @Schema(description = "Indicates if this locale is the default one", example = "true")
    private Boolean isDefault;

    @Schema(description = "Indicates if the locale is currently active", example = "true")
    private Boolean isActive;

    public LocaleResponse(Locale locale) {
        this.id = locale.getId();
        this.localeCode = locale.getLocaleCode();
        this.localeName = locale.getLocaleName();
        this.localeLanguage = locale.getLocaleLanguage();
        this.isDefault = locale.getIsDefault();
        this.isActive = locale.getIsActive();
    }
}