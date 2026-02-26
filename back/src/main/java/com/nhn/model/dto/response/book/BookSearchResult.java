package com.nhn.model.dto.response.book;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchResult {
    private UUID id;

    private String name;

    private String slug;

    private Long price;

    private Integer discount;

    private Integer quantity;

    private String imageUrl;

    private Date createdAt;

    private Date updatedAt;

    private String categoryName;

    private String publisherName;

    private String authorNames;
}
