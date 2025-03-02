package com.tms.app.dtos.locale.request;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocaleRequest {
    private String localeCode;
    private String localeName;
    private String localeLanguage;
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