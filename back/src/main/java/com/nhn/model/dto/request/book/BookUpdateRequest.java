package com.nhn.model.dto.request.book;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class BookUpdateRequest extends BookCreateRequest {
    private List<UUID> keepImageIds;
}
