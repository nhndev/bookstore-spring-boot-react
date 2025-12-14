package com.nhn.model.dto.response.book.category;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCategoryInfo {
    private UUID id;

    private String name;

    private String slug;

    private String imageUrl;

    private String parentId;

    private String parentName;

    private Date updatedAt;
}
