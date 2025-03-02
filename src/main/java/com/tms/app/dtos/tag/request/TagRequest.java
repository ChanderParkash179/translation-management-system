package com.tms.app.dtos.tag.request;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {

    private String tagName;
    private String tagDescription;

    @Override
    public String toString() {
        return "TagRequest{" +
                "tagName='" + tagName + '\'' +
                ", tagDescription='" + tagDescription + '\'' +
                '}';
    }
}