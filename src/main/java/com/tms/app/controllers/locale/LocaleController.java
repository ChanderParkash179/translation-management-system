package com.tms.app.controllers.locale;

import com.tms.app.dtos.locale.request.LocaleRequest;
import com.tms.app.dtos.locale.response.LocaleResponse;
import com.tms.app.dtos.wrapper.APIResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.enums.Message;
import com.tms.app.services.locale.LocaleService;
import com.tms.app.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/locale")
@Tag(name = "Locale Management", description = "Endpoints for managing locale settings")
public class LocaleController {

    private final LocaleService localeService;

    @PostMapping
    @Operation(summary = "Create a new locale", description = "Creates a new locale entry with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locale successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<LocaleResponse>> create(@Valid @RequestBody LocaleRequest request) {
        LocaleResponse response = this.localeService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_CREATED.getMessage(), response));
    }

    @PatchMapping
    @Operation(summary = "Update an existing locale", description = "Updates an existing locale with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locale successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Locale not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<LocaleResponse>> update(@Valid @RequestBody LocaleRequest request) {
        LocaleResponse response = this.localeService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_UPDATED.getMessage(), response));
    }

    @GetMapping("/{id}/id")
    @Operation(summary = "Get locale by ID", description = "Retrieves a locale by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locale found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "Locale not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<LocaleResponse>> findById(@PathVariable("id") UUID id) {
        LocaleResponse response = this.localeService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_FOUNDED_BY_ID.getMessage(), response));
    }

    @GetMapping("/{code}/code")
    @Operation(summary = "Get locale by code", description = "Retrieves a locale by its unique code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locale found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "Locale not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<LocaleResponse>> findByCode(@PathVariable("code") String code) {
        LocaleResponse response = this.localeService.findByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_FOUNDED_BY_CODE.getMessage(), response));
    }

    @GetMapping
    @Operation(summary = "Get all locales", description = "Retrieves a paginated list of all locales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locales retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)))
    })
    public ResponseEntity<APIResponse<PaginationResponse<LocaleResponse>>> findAll(
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_NO) Integer pageNo,
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize
    ) {
        PaginationResponse<LocaleResponse> response = this.localeService.findAll(pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOCALE_LISTED.getMessage(), response));
    }
}