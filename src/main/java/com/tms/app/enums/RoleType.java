package com.tms.app.enums;

import lombok.Getter;

@Getter
public enum RoleType {

    ADMIN("ADMIN"),
    USER("USER");

    private final String roleType;

    RoleType(String roleType) {
        this.roleType = roleType;
    }
}
