package com.careercrack.careercrack.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A page of results with only the fields a client uses (Spring's own Page also sends pageable and sort objects).
 * The names match Spring's, so code that reads content, number, totalPages and totalElements keeps working.
 */
@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private final List<T> content;
    // zero-based page number
    private final int number;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}
