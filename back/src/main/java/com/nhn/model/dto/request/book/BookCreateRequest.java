package com.nhn.model.dto.request.book;

import java.util.List;
import java.util.UUID;

import com.nhn.annotation.validation.MaxLength;
import com.nhn.annotation.validation.Required;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookCreateRequest {
    @Required(message = "Tên sách là trường bắt buộc")
    @MaxLength(value = 255, message = "Vui lòng nhập tên sách không quá 255 ký tự")
    private String name;

    private String description;

    @Required(message = "Giá sách là trường bắt buộc")
    private Long price;

    @Required(message = "Giảm giá là trường bắt buộc")
    private Integer discount;

    @Required(message = "Số lượng là trường bắt buộc")
    private Integer quantity;

    private Integer publishYear;

    private String language;

    private Integer weight;

    private String size;

    private Integer pageCount;

    private String layout;

    @Required(message = "Danh mục sách là trường bắt buộc")
    private UUID categoryId;

    @Required(message = "Nhà xuất bản là trường bắt buộc")
    private UUID publisherId;

    @Data
    public static class BookAuthorRequest {
        @Required(message = "Mã tác giả là trường bắt buộc")
        private UUID authorId;

        private Boolean isMain;
    }

    @Required(message = "Tác giả là trường bắt buộc")
    @Valid
    private List<BookAuthorRequest> authors;
}
