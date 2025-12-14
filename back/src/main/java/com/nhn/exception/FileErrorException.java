package com.nhn.exception;

import java.io.Serial;

import com.nhn.model.dto.response.BaseErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8575155368489122731L;

    private final BaseErrorResponse res;
}
