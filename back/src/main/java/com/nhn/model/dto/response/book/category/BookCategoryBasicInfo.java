package com.nhn.model.dto.response.book.category;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookCategoryBasicInfo {
    private UUID id;

    private String name;
}
