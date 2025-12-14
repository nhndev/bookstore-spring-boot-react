package com.nhn.exception;

import java.io.Serial;

import com.nhn.model.dto.response.BaseErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ForbiddenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6526420751006125781L;


    private final BaseErrorResponse res;
}
