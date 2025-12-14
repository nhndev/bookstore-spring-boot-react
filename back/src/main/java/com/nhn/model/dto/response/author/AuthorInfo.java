package com.nhn.model.dto.response.author;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorInfo {
    private UUID id;

    private String name;

    private String slug;
}
