package com.nhn.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatusEnum {
    INACTIVE(0), ACTIVE(1);

    private final int code;
}
