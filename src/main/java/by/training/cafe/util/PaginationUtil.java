package by.training.cafe.util;

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
}
