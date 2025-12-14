package com.nhn.repository.book;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhn.model.entity.book.BookPublisher;

@Repository
public interface BookPublisherRepository extends JpaRepository<BookPublisher, UUID> {
    Optional<BookPublisher> findBySlug(String slug);
}
