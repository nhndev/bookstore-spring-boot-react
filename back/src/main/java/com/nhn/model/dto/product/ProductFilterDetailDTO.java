package com.nhn.model.dto.product;

import java.util.UUID;

import com.nhn.model.dto.request.book.category.ProductCategoryBasicDTO;

import lombok.Data;

@Data
public class ProductFilterDetailDTO {
    private UUID id;

    private String name;

    private String slug;

    private Long price;

    private int discount;

    private int quantity;

    private String imageUrl;

    private ProductCategoryBasicDTO productCategory;
}
