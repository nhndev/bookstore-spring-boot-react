package com.nhn.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseErrorResponse {
    private String errorCode;

    private String errorMessage;

    private List<ErrorMessage> errorMessages;

    @Builder.Default
    private long responseTime = System.currentTimeMillis();
}
