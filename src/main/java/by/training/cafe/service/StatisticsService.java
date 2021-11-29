package by.training.cafe.service;

import by.training.cafe.dto.DishDto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The class {@code StatisticsService} is a class that
 * extends {@link Service}.<br/>
 * Provides different statistics.
 *
 * @author Nikita Romanov
 * @see Service
 */
public interface StatisticsService extends Service {

    /**
     * Calculates sum of the all completed orders for current month.
     *
     * @return sum of the all completed orders for current month.
     * @throws ServiceException if DaoException occurred.
     */
    long earnedLastMonth() throws ServiceException;

    /**
     * Calculates sum of the all completed orders for previous month.
     *
     * @return sum of the all completed orders for previous month.
     * @throws ServiceException if DaoException occurred.
     */
    long earnedThisMonth() throws ServiceException;

    /**
     * Calculates sum of the all completed orders.
     *
     * @return sum of the all completed orders.
     * @throws ServiceException if DaoException occurred.
     */
    long earnedTotal() throws ServiceException;

    /**
     * Finds top dishes and their total count and total price.
     *
     * @param limit limit top dishes.
     * @return map where key is dishDto and value is an entry
     * that contains total price as a key and total count as a value.
     * @throws ServiceException if DaoException occurred or limit is invalid.
     */
    Map<DishDto, Entry<Long, Long>> findTopDishes(long limit)
            throws ServiceException;

    /**
     * Returns date with the first day of month by the given date.
     *
     * @param date date to find its first day.
     * @return date with the first day of the month.
     * @throws ServiceException if DaoException occurred or date is invalid.
     */
    LocalDate getFirstDayOfMonth(LocalDate date) throws ServiceException;

    /**
     * Returns date with the last day of month by the given date.
     *
     * @param date date to find its last day.
     * @return date with the last day of the month.
     * @throws ServiceException if DaoException occurred or date is invalid.
     */
    LocalDate getLastDayOfMonth(LocalDate date) throws ServiceException;
}
