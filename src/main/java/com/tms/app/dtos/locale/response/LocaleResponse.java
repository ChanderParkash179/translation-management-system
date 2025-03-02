package com.tms.app.dtos.locale.response;

import com.tms.app.entities.locale.Locale;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocaleResponse {
    private UUID id;
    private String localeCode;
    private String localeName;
    private String localeLanguage;
    private Boolean isDefault;
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