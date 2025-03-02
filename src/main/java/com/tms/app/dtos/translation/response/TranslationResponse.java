package com.tms.app.dtos.translation.response;

import com.tms.app.dtos.locale.response.LocaleResponse;
import com.tms.app.dtos.tag.response.TagResponse;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationResponse {

    private UUID id;
    private String translationKey;
    private String content;
    private LocaleResponse locale;
    private List<TagResponse> tags;
    private Boolean isActive;
}