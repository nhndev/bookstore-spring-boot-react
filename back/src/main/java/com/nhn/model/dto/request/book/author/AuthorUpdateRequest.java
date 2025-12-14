package com.nhn.model.dto.request.book.author;

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
public class AuthorUpdateRequest {
    @Required(message = "Tên tác giả là trường bắt buộc")
    @MaxLength(value = 255, message = "Vui lòng nhập tên tác giả không quá 255 ký tự")
    private String name;
}
