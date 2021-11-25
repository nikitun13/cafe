package by.training.cafe.service;

import java.util.Map;
import java.util.function.LongSupplier;

public interface PaginationService extends Service {

    boolean isValidCurrentPage(long currentPage, long totalPages);

    default long isValidCurrentPageOrElseGet(long currentPage, long totalPages,
                                             LongSupplier supplier) {
        return isValidCurrentPage(currentPage, totalPages)
                ? currentPage
                : supplier.getAsLong();
    }

    long calculateTotalPages(long totalRows, long limit);

    long calculateOffset(long limit, long currentPage);

    Map.Entry<Long, Long> calculateStartAndEndPage(long currentPage,
                                                   long totalPages,
                                                   int pageOffset);
}
