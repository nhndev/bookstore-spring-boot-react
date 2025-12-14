package com.nhn.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationResponse<T> {
    private List<T> data;

    private int limit;

    private int currentPage;

    private int totalItems;

    private int totalPages;
}
