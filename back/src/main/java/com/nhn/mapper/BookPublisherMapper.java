package com.nhn.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.nhn.model.dto.request.book.publisher.BookPublisherSearchRequest;
import com.nhn.model.entity.book.BookPublisher;

@Mapper
public interface BookPublisherMapper {
    List<BookPublisher> search(BookPublisherSearchRequest request);

    Integer count(BookPublisherSearchRequest request);
}
