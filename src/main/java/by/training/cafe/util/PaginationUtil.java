package by.training.cafe.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public final class PaginationUtil {

    private static final long DEFAULT_PAGE = 1L;

    private PaginationUtil() {
    }

    public static long calculateOffset(long limit, long currentPage) {
        return limit * (currentPage - 1);
    }

    public static long calculateNumberOfPages(long totalRows, long limit) {
        return (long) Math.ceil((double) totalRows / limit);
    }

    public static long checkCurrentPageIsInRangeOfTotalPagesOrElseDefault(
            long currentPage, long totalPages) {
        return totalPages >= currentPage
                ? currentPage
                : DEFAULT_PAGE;
    }

    public static long parsePageOrElseDefault(String page) {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        try {
            long parsedPage = Long.parseLong(page);
            return parsedPage > 0
                    ? parsedPage
                    : DEFAULT_PAGE;
        } catch (NumberFormatException e) {
            return DEFAULT_PAGE;
        }
    }

    public static Entry<Long, Long> calculateStartAndEndPage(long currentPage,
                                                             long totalPages) {
        long startPage = currentPage - 2L;
        long endPage = currentPage + 2L;
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
