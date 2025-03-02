package com.tms.app.services.translation;

import com.tms.app.dtos.translation.request.TranslationRequest;
import com.tms.app.dtos.translation.response.TranslationResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TranslationService {
    TranslationResponse create(@Valid TranslationRequest request);

    TranslationResponse get(String key, String localeCode);

    TranslationResponse update(UUID id, @Valid TranslationRequest request);

    PaginationResponse<TranslationResponse> search(String key, List<String> tags, String localeCode, int pageNo, int pageSize);

    Map<String, String> export(String localeCode);
}
