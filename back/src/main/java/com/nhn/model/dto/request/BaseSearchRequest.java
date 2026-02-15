package com.nhn.model.dto.request;

import com.nhn.annotation.validation.Required;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class BaseSearchRequest {
    @Required(message = "pageNum is required")
    private Integer pageNum;

    @Required(message = "pageSize is required")
    private Integer pageSize;

    private String sortBy;

    private String sortOrder;
}
