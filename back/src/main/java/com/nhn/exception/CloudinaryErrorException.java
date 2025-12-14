package com.nhn.exception;

import java.io.Serial;

import com.nhn.model.dto.response.BaseErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CloudinaryErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8512759523847969848L;

    private final BaseErrorResponse res;
}
