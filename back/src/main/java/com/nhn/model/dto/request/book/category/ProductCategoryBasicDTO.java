package com.nhn.model.dto.request.book.category;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCategoryBasicDTO {
    private UUID id;

    private String name;

    private String code;

    private UUID parentId;
}
