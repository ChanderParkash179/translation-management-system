package com.tms.app.controllers.translation;

import com.tms.app.dtos.translation.request.TranslationRequest;
import com.tms.app.dtos.translation.response.TranslationResponse;
import com.tms.app.dtos.wrapper.APIResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.enums.Message;
import com.tms.app.services.translation.TranslationService;
import com.tms.app.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/translation")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public ResponseEntity<APIResponse<TranslationResponse>> create(@Valid @RequestBody TranslationRequest request) {
        TranslationResponse response = this.translationService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_CREATED.getMessage(), response));
    }

    @GetMapping("/{key}")
    public ResponseEntity<APIResponse<TranslationResponse>> get(@PathVariable String key, @RequestParam String localeCode) {
        TranslationResponse response = this.translationService.get(key, localeCode);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_FOUNDED_BY_KEY_WITH_OR_WITHOUT_CODE.getMessage(), response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<TranslationResponse>> update(@PathVariable UUID id, @Valid @RequestBody TranslationRequest request) {
        TranslationResponse response = this.translationService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_UPDATED.getMessage(), response));
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse<PaginationResponse<TranslationResponse>>> search(
            @RequestParam(required = false) String key,
            @RequestParam(required = false) List<String> tags,
            @RequestParam String localeCode,
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_NO) Integer pageNo,
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize) {

        PaginationResponse<TranslationResponse> response = this.translationService.search(key, tags, localeCode, pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_LISTED.getMessage(), response));
    }

    @GetMapping("/export")
    public ResponseEntity<APIResponse<Map<String, String>>> export(@RequestParam String localeCode) {
        Map<String, String> response = this.translationService.export(localeCode);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_EXPORT.getMessage(), response));
    }
}