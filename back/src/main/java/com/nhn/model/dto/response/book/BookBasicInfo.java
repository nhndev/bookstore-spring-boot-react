package com.nhn.model.dto.response.book;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookBasicInfo {
    private UUID id;

    private String name;

    private String slug;

    private Long price;

    private int discount;

    private int quantity;

    private String imageUrl;
}
