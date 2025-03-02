package com.tms.app.controllers.tag;

import com.tms.app.dtos.tag.request.TagRequest;
import com.tms.app.dtos.tag.response.TagResponse;
import com.tms.app.dtos.wrapper.APIResponse;
import com.tms.app.dtos.wrapper.PaginationResponse;
import com.tms.app.enums.Message;
import com.tms.app.services.tag.TagService;
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
@RequestMapping("/api/v1/tag")
@Tag(name = "Tag Management", description = "Endpoints for managing tags")
public class TagController {

    private final TagService tagService;

    @PostMapping
    @Operation(summary = "Create a new tag", description = "Creates a new tag with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<TagResponse>> create(@Valid @RequestBody TagRequest request) {
        TagResponse response = this.tagService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_CREATED.getMessage(), response));
    }

    @PatchMapping
    @Operation(summary = "Update an existing tag", description = "Updates an existing tag with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<TagResponse>> update(@Valid @RequestBody TagRequest request) {
        TagResponse response = this.tagService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_UPDATED.getMessage(), response));
    }

    @GetMapping("/{id}/id")
    @Operation(summary = "Get tag by ID", description = "Retrieves a tag by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<TagResponse>> findById(@PathVariable("id") UUID id) {
        TagResponse response = this.tagService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_FOUNDED_BY_ID.getMessage(), response));
    }

    @GetMapping("/{name}/name")
    @Operation(summary = "Get tag by name", description = "Retrieves a tag by its unique name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<TagResponse>> findByName(@PathVariable("name") String name) {
        TagResponse response = this.tagService.findByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_FOUNDED_BY_NAME.getMessage(), response));
    }

    @GetMapping
    @Operation(summary = "Get all tags", description = "Retrieves a paginated list of all tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tags retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)))
    })
    public ResponseEntity<APIResponse<PaginationResponse<TagResponse>>> findAll(
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_NO) Integer pageNo,
            @RequestParam(required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize
    ) {
        PaginationResponse<TagResponse> response = this.tagService.findAll(pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.TAG_LISTED.getMessage(), response));
    }
}