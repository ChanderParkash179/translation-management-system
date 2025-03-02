package com.tms.app.dtos.locale.request;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating locale details")
public class LocaleRequest {

    @Schema(description = "Code representing the locale", example = "en_US")
    private String localeCode;

    @Schema(description = "Human-readable name of the locale", example = "English (United States)")
    private String localeName;

    @Schema(description = "Language associated with the locale", example = "English")
    private String localeLanguage;

    @Schema(description = "Indicates if this locale should be set as default", example = "true")
    private Boolean isDefault;

    @Override
    public String toString() {
        return "LocaleRequest{" +
                "localeCode='" + localeCode + '\'' +
                ", localeName='" + localeName + '\'' +
                ", localeLanguage='" + localeLanguage + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}