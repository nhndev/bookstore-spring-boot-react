package com.nhn.repository.book;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhn.model.entity.book.BookCategory;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, UUID> {
    Optional<BookCategory> findBySlug(String slug);
}
