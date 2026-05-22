package com.nhn.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

import com.nhn.model.dto.request.book.category.BookCategorySearchRequest;
import com.nhn.model.dto.response.book.category.BookCategoryInfo;

@Mapper
public interface BookCategoryMapper {
    List<BookCategoryInfo> search(BookCategorySearchRequest request);

    Integer count(BookCategorySearchRequest request);

    /**
     * Count books that reference the given category or any of its direct children. If
     * result > 0, the category cannot be deleted.
     */
    int countBooksReferencingCategoryOrItsChildren(UUID categoryId);
}
