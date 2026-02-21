package fr.lucasbmmn.overhearrserver.common.dto;

import org.springframework.data.domain.Page;
import java.util.List;

/**
 * Generic wrapper for paginated API responses.
 *
 * @param data The list of items on the current page.
 * @param meta Pagination metadata (page number, size, totals).
 * @param <T>  The type of items contained in the page.
 */
public record PageResponse<T>(
        List<T> data,
        PageMetadata meta) {
        /**
         * Metadata describing the pagination state.
         *
         * @param page          The zero-based page index.
         * @param size          The maximum number of items per page.
         * @param totalElements The total number of items across all pages.
         * @param totalPages    The total number of pages.
         */
        public record PageMetadata(
                int page,
                int size,
                long totalElements,
                int totalPages) {
        }

        /**
         * Factory method that creates a {@link PageResponse} from a Spring Data {@link Page}.
         *
         * @param page The source page.
         * @param <T>  The type of items.
         * @return A new {@link PageResponse} mirroring the given page.
         */
        public static <T> PageResponse<T> from(Page<T> page) {
            return new PageResponse<>(
                    page.getContent(),
                    new PageMetadata(
                            page.getNumber(),
                            page.getSize(),
                            page.getTotalElements(),
                            page.getTotalPages()));
        }
}