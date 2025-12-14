package com.nhn.exception;

import java.io.Serial;

import com.nhn.model.dto.response.BaseErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FuncErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5411552099021098909L;

    private final BaseErrorResponse res;
}
