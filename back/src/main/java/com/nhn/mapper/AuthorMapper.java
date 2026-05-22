package com.nhn.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

import com.nhn.model.dto.request.book.author.AuthorSearchRequest;
import com.nhn.model.entity.book.Author;

@Mapper
public interface AuthorMapper {
    List<Author> search(AuthorSearchRequest request);

    Integer count(AuthorSearchRequest request);

    int countBooksReferencingAuthor(UUID authorId);
}
