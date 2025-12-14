package com.nhn.repository.book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nhn.model.entity.book.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Optional<Author> findBySlug(String slug);

    @Query("SELECT a.id FROM Author a WHERE a.id IN (:ids)")
    List<UUID> findAuthorIdsIn(List<UUID> ids);
}
