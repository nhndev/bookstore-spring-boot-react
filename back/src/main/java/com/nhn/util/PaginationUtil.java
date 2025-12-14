package com.nhn.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PaginationUtil {
    public static Pageable buildPageable(final int pageNum, final int pageSize) {
        return PageRequest.of(pageNum - 1, pageSize);
    }
}
