package com.nhn.model.dto.response.book.publisher;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookPublisherInfo {
    private UUID id;

    private String name;

    private String slug;
}
