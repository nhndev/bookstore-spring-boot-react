package com.nhn.model.dto.request.book.category;

import com.nhn.annotation.validation.Required;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookCategorySearchRequest {
    private String name;

    @Required(message = "pageNum is required")
    private Integer pageNum;

    @Required(message = "pageSize is required")
    private Integer pageSize;
}
