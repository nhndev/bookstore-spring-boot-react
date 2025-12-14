package com.nhn.repository.book;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nhn.model.entity.book.BookAuthor;


@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthor.PrimaryKeys> {
    @Modifying
    @Query("DELETE FROM BookAuthor ba WHERE ba.bookId = :bookId")
    void deleteByBookId(final UUID bookId);
}
