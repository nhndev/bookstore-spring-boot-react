package com.nhn.model.dto.request.book;

import java.util.UUID;

import com.nhn.model.dto.request.BaseSearchRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchRequest extends BaseSearchRequest {
    private String name;

    private UUID categoryId;

    private UUID publisherId;
}
