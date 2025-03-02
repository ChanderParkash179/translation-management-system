package com.tms.app.controllers.tag;

import com.tms.app.dtos.tag.request.TagRequest;
import com.tms.app.dtos.tag.response.TagResponse;
import com.tms.app.dtos.wrapper.APIResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.enums.Message;
import com.tms.app.services.tag.TagService;
import com.tms.app.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tag")
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<APIResponse<TagResponse>> create(@Valid @RequestBody TagRequest request) {
        TagResponse response = this.tagService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_CREATED.getMessage(), response));
    }

    @PatchMapping
    public ResponseEntity<APIResponse<TagResponse>> update(@Valid @RequestBody TagRequest request) {
        TagResponse response = this.tagService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_UPDATED.getMessage(), response));
    }

    @GetMapping("/{id}/id")
    public ResponseEntity<APIResponse<TagResponse>> findById(@PathVariable("id") UUID id) {
        TagResponse response = this.tagService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_FOUNDED_BY_ID.getMessage(), response));
    }

    @GetMapping("/{name}/name")
    public ResponseEntity<APIResponse<TagResponse>> findByName(@PathVariable("name") String name) {
        TagResponse response = this.tagService.findByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_FOUNDED_BY_NAME.getMessage(), response));
    }

    @GetMapping
    public ResponseEntity<APIResponse<PaginationResponse<TagResponse>>> findAll(
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_NO) Integer pageNo,
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize
    ) {
        PaginationResponse<TagResponse> response = this.tagService.findAll(pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_LISTED.getMessage(), response));
    }
}