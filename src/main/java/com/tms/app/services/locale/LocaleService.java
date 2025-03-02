package com.tms.app.services.locale;

import com.tms.app.dtos.locale.request.LocaleRequest;
import com.tms.app.dtos.locale.response.LocaleResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;

import java.util.UUID;

public interface LocaleService {

    LocaleResponse create(LocaleRequest request);

    LocaleResponse findById(UUID id);

    LocaleResponse findByCode(String code);

    PaginationResponse<LocaleResponse> findAll(Integer pageNo, Integer pageSize);

    LocaleResponse update(LocaleRequest request);
}