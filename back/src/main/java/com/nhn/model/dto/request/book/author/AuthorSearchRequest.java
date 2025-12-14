package com.nhn.model.dto.request.book.author;

import com.nhn.model.dto.request.BaseSearchRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class AuthorSearchRequest extends BaseSearchRequest {
    private String name;
}
