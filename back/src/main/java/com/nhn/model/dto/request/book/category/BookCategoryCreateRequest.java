package com.nhn.model.dto.request.book.category;

import java.util.UUID;

import com.nhn.annotation.validation.MaxLength;
import com.nhn.annotation.validation.Required;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookCategoryCreateRequest {
    @Required(message = "Tên danh mục là trường bắt buộc")
    @MaxLength(value = 255, message = "Vui lòng nhập tên danh mục không quá 255 ký tự")
    private String name;

    private UUID parentId;
}
