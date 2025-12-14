package com.nhn.model.dto.product.productImage;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductImageBasicDTO {

    private UUID id;

    private String url;

    private String publicId;

}
