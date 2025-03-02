package com.tms.app.services.tag;

import com.tms.app.dtos.tag.request.TagRequest;
import com.tms.app.dtos.tag.response.TagResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;

import java.util.UUID;

public interface TagService {

    TagResponse create(TagRequest request);

    TagResponse findById(UUID id);

    TagResponse findByName(String name);

    PaginationResponse<TagResponse> findAll(Integer pageNo, Integer pageSize);

    TagResponse update(TagRequest request);
}