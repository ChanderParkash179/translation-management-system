package com.tms.app.controllers.locale;

import com.tms.app.dtos.locale.request.LocaleRequest;
import com.tms.app.dtos.locale.response.LocaleResponse;
import com.tms.app.dtos.wrapper.APIResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.enums.Message;
import com.tms.app.services.locale.LocaleService;
import com.tms.app.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/locale")
public class LocaleController {

    private final LocaleService localeService;

    @PostMapping
    public ResponseEntity<APIResponse<LocaleResponse>> create(@Valid @RequestBody LocaleRequest request) {
        LocaleResponse response = this.localeService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_CREATED.getMessage(), response));
    }

    @PatchMapping
    public ResponseEntity<APIResponse<LocaleResponse>> update(@Valid @RequestBody LocaleRequest request) {
        LocaleResponse response = this.localeService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_UPDATED.getMessage(), response));
    }

    @GetMapping("/{id}/id")
    public ResponseEntity<APIResponse<LocaleResponse>> findById(@PathVariable("id") UUID id) {
        LocaleResponse response = this.localeService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_FOUNDED_BY_ID.getMessage(), response));
    }

    @GetMapping("/{code}/code")
    public ResponseEntity<APIResponse<LocaleResponse>> findByCode(@PathVariable("code") String code) {
        LocaleResponse response = this.localeService.findByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_FOUNDED_BY_ID.getMessage(), response));
    }

    @GetMapping
    public ResponseEntity<APIResponse<PaginationResponse<LocaleResponse>>> findAll(
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_NO) Integer pageNo,
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize
    ) {
        PaginationResponse<LocaleResponse> response = this.localeService.findAll(pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_LISTED.getMessage(), response));
    }
}