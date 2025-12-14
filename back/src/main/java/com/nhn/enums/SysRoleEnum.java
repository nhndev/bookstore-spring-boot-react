package com.nhn.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysRoleEnum {
    SUPER_ADMIN("SUPER_ADMIN"),
    CUSTOMER("CUSTOMER");

    private final String code;
}

