package com.nhn.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.nhn.model.dto.request.book.category.BookCategorySearchRequest;
import com.nhn.model.dto.response.book.category.BookCategoryInfo;

@Mapper
public interface BookCategoryMapper {
    List<BookCategoryInfo> search(BookCategorySearchRequest request);

    Integer count(BookCategorySearchRequest request);
}
