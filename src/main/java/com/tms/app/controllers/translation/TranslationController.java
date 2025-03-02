package com.tms.app.controllers.translation;

import com.tms.app.dtos.translation.request.TranslationRequest;
import com.tms.app.dtos.translation.response.TranslationResponse;
import com.tms.app.dtos.wrapper.APIResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.enums.Message;
import com.tms.app.services.translation.TranslationService;
import com.tms.app.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Translation Management", description = "Endpoints for managing translations")
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    @Operation(summary = "Create a new translation", description = "Creates a new translation entry with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translation successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<TranslationResponse>> create(@Valid @RequestBody TranslationRequest request) {
        TranslationResponse response = this.translationService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_CREATED.getMessage(), response));
    }

    @GetMapping("/{key}")
    @Operation(summary = "Get translation by key and locale", description = "Retrieves a translation by its key and locale code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translation found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "Translation not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<TranslationResponse>> get(
            @Parameter(description = "Translation key", example = "welcome.message") @PathVariable String key,
            @Parameter(description = "Locale code", example = "en_US") @RequestParam String localeCode) {
        TranslationResponse response = this.translationService.get(key, localeCode);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_FOUNDED_BY_KEY_WITH_OR_WITHOUT_CODE.getMessage(), response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing translation", description = "Updates a translation identified by its ID with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translation successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Translation not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<TranslationResponse>> update(
            @Parameter(description = "Translation ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
            @Valid @RequestBody TranslationRequest request) {
        TranslationResponse response = this.translationService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_UPDATED.getMessage(), response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search translations", description = "Retrieves a paginated list of translations based on key, tags, and locale code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translations retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)))
    })
    public ResponseEntity<APIResponse<PaginationResponse<TranslationResponse>>> search(
            @Parameter(description = "Translation key (optional)", example = "welcome") @RequestParam(required = false) String key,
            @Parameter(description = "List of tags (optional)", example = "[\"web\", \"mobile\"]") @RequestParam(required = false) List<String> tags,
            @Parameter(description = "Locale code", example = "en_US") @RequestParam String localeCode,
            @Parameter(description = "Page number", example = "1") @RequestParam(required = false, defaultValue = AppConstants.PAGE_NO) Integer pageNo,
            @Parameter(description = "Page size", example = "10") @RequestParam(required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize) {
        PaginationResponse<TranslationResponse> response = this.translationService.search(key, tags, localeCode, pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_LISTED.getMessage(), response));
    }

    @GetMapping("/export")
    @Operation(summary = "Export translations", description = "Exports all translations for a given locale code as a key-value map")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translations exported successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)))
    })
    public ResponseEntity<APIResponse<Map<String, String>>> export(
            @Parameter(description = "Locale code", example = "en_US") @RequestParam String localeCode) {
        Map<String, String> response = this.translationService.export(localeCode);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TRANSLATION_EXPORT.getMessage(), response));
    }
}