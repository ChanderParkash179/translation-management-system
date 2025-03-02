package com.tms.app.dtos.tag.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating tag details")
public class TagRequest {

    @Schema(description = "Name of the tag", example = "web")
    private String tagName;

    @Schema(description = "Description of the tag", example = "Tags related to technological topics")
    private String tagDescription;

    @Override
    public String toString() {
        return "TagRequest{" +
                "tagName='" + tagName + '\'' +
                ", tagDescription='" + tagDescription + '\'' +
                '}';
    }
}