package com.nhn.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.nhn.model.dto.request.book.BookSearchRequest;
import com.nhn.model.dto.response.book.BookSearchResult;

@Mapper
public interface BookMapper {
    List<BookSearchResult> search(BookSearchRequest request);

    Integer count(BookSearchRequest request);
}
