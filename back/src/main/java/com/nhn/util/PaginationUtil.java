package com.nhn.util;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.nhn.model.dto.request.BaseSearchRequest;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PaginationUtil {
    /** Table name: bs_publishers. */
    public static final String TABLE_BS_PUBLISHERS = "bs_publishers";

    /** Table name: bs_authors. */
    public static final String TABLE_BS_AUTHORS = "bs_authors";

    public static final String CREATED_AT = "created_at";

    private static final Map<String, Set<String>> ALLOWED_SORT_COLUMNS = Map.of(TABLE_BS_PUBLISHERS,
                                                                                Set.of("id",
                                                                                       "name",
                                                                                       "slug",
                                                                                       "created_at",
                                                                                       "updated_at"),
                                                                                TABLE_BS_AUTHORS,
                                                                                Set.of("id",
                                                                                       "name",
                                                                                       "slug",
                                                                                       "created_at",
                                                                                       "updated_at"));

    public static Pageable buildPageable(final int pageNum,
                                         final int pageSize) {
        return PageRequest.of(pageNum - 1, pageSize);
    }

    public static void applySortParams(final BaseSearchRequest request,
                                       final String table,
                                       final String defaultSortBy,
                                       final String defaultSortOrder) {
        final String sortByCamel = request.getSortBy();
        if (StringUtils.isNotBlank(sortByCamel)) {
            final String sortBySnake = StringUtil.camelToSnakeCase(StringUtils.trim(sortByCamel));
            if (isValidSortColumn(table, sortBySnake)) {
                request.setSortBy(sortBySnake);
            } else {
                request.setSortBy(defaultSortBy);
            }
        }

        final String order = request.getSortOrder();
        if (StringUtils.equalsAnyIgnoreCase(order, Sort.Direction.ASC.name(),
                                            Sort.Direction.DESC.name())) {
            request.setSortOrder(order);
        } else {
            request.setSortOrder(defaultSortOrder);
        }
    }

    /**
     * Checks if the given column name (snake_case) is allowed for sorting on the table.
     *
     * @param  tableName   table identifier (e.g. {@link #TABLE_BS_PUBLISHERS})
     * @param  columnSnake column name in snake_case, already converted from client input
     * @return             true if the column is allowed for sort, false otherwise
     */
    public static boolean isValidSortColumn(final String tableName,
                                            final String columnSnake) {
        if (StringUtils.isAnyBlank(tableName, columnSnake)) {
            return false;
        }
        final Set<String> allowed = ALLOWED_SORT_COLUMNS.get(tableName);
        return Objects.nonNull(allowed) && allowed.contains(columnSnake.trim());
    }
}
