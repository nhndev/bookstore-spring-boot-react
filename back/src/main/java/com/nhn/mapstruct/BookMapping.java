package com.nhn.mapstruct;

import org.mapstruct.Mapper;

import com.nhn.model.dto.request.book.BookCreateRequest;
import com.nhn.model.dto.response.book.BookBasicInfo;
import com.nhn.model.entity.book.Author;
import com.nhn.model.entity.book.Book;
import com.nhn.model.entity.book.BookCategory;
import com.nhn.model.entity.book.BookPublisher;

@Mapper(componentModel = "spring")
public interface BookMapping {
    BookCategory toBookCategory(BookCategory source);

    Author toAuthor(Author source);

    BookPublisher toBookPublisher(BookPublisher source);

    Book toBook(Book source);

    Book toBook(BookCreateRequest source);

    BookBasicInfo toBookBasicInfo(Book source);
}
