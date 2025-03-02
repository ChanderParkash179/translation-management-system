package com.tms.app.dtos.tag.response;

import lombok.*;
import com.tms.app.entities.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing tag details")
public class TagResponse {

    @Schema(description = "Unique identifier of the tag", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private UUID id;

    @Schema(description = "Name of the tag", example = "web")
    private String tagName;

    @Schema(description = "Description of the tag", example = "Tags related to technological topics")
    private String tagDescription;

    @Schema(description = "Indicates if the tag is currently active", example = "true")
    private Boolean isActive;

    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.tagName = tag.getTagName();
        this.tagDescription = tag.getTagDescription();
        this.isActive = tag.getIsActive();
    }
}