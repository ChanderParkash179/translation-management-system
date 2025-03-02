package com.tms.app.dtos.tag.response;

import com.tms.app.entities.tag.Tag;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {
    private UUID id;
    private String tagName;
    private String tagDescription;
    private Boolean isActive;

    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.tagName = tag.getTagName();
        this.tagDescription = tag.getTagDescription();
        this.isActive = tag.getIsActive();
    }
}