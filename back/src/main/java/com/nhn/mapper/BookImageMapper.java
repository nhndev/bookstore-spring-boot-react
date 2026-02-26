package com.nhn.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

import com.nhn.model.entity.book.BookImage;

@Mapper
public interface BookImageMapper {
    List<BookImage> getAllByBookId(UUID bookId);

    BookImage getFirstByBookId(UUID bookId);
}
