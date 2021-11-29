package by.training.cafe.service.impl;

import by.training.cafe.service.PaginationService;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

/**
 * The class {@code PaginationServiceImpl} is a class
 * that implements {@link PaginationService}.
 *
 * @author Nikita Romanov
 * @see PaginationService
 */
public class PaginationServiceImpl implements PaginationService {

    @Override
    public boolean isValidCurrentPage(long currentPage, long totalPages) {
        return currentPage > 0 && totalPages >= currentPage;
    }

    @Override
    public long calculateTotalPages(long totalRows, long limit) {
        return (long) Math.ceil((double) totalRows / limit);
    }

    @Override
    public long calculateOffset(long limit, long currentPage) {
        return limit * (currentPage - 1);
    }

    @Override
    public Map.Entry<Long, Long> calculateStartAndEndPage(long currentPage,
                                                          long totalPages,
                                                          int pageOffset) {
        long startPage = currentPage - pageOffset;
        long endPage = currentPage + pageOffset;
        if (startPage < 1L) {
            endPage += Math.abs(startPage) + 1L;
            startPage = 1L;
        } else if (endPage > totalPages) {
            startPage -= endPage - totalPages;
            endPage = totalPages;
        }
        return new SimpleEntry<>(
                Math.max(startPage, 1L),
                Math.min(endPage, totalPages));
    }
}
