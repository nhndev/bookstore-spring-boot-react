package com.nhn.model.dto.request.book.publisher;

import com.nhn.annotation.validation.MaxLength;
import com.nhn.annotation.validation.Required;

import lombok.Data;

@Data
public class BookPublisherUpdateRequest {
    @Required(message = "Tên nhà xuất bản là trường bắt buộc")
    @MaxLength(value = 255, message = "Vui lòng nhập tên nhà xuất bản không quá 255 ký tự")
    private String name;
}