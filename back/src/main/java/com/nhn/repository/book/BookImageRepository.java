package com.nhn.repository.book;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhn.model.entity.book.BookImage;

@Repository
public interface BookImageRepository extends JpaRepository<BookImage, UUID> {}
