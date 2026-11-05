package com.careercrack.careercrack.utils;

import com.careercrack.careercrack.exceptions.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;
import java.util.TreeSet;

public final class PageRequests {

    public static final int MAX_PAGE_SIZE = 100;

    private PageRequests() {
    }

    /**
     * Builds a Pageable from request parameters.
     * sortBy must be one of allowedSortFields (entity property names): Spring would otherwise try to sort by whatever
     * the client sends, which fails with a 500 or sorts by fields the client should not use.
     * Ties are broken by id, so rows never repeat or go missing between pages.
     */
    public static Pageable of(int page, int size, String sortBy, boolean ascending, Set<String> allowedSortFields) {
        if (page < 0) {
            throw new BadRequestException("Page cannot be negative");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("Size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (sortBy == null || !allowedSortFields.contains(sortBy)) {
            throw new BadRequestException("Cannot sort by '" + sortBy + "'. Use one of: "
                    + String.join(", ", new TreeSet<>(allowedSortFields)));
        }
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        if (!"id".equals(sortBy)) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "id"));
        }
        return PageRequest.of(page, size, sort);
    }
}
