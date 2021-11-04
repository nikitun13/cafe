package by.training.cafe.dao;

import by.training.cafe.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface {@code OrderDao} is an interface that
 * extends {@link BaseDao} and provides new operations with
 * {@link Order} entity.
 *
 * @author Nikita Romanov
 * @see BaseDao
 * @see Order
 */
public interface OrderDao extends BaseDao<Long, Order> {

    /**
     * Finds {@code orders} by {@code status}.
     *
     * @param status {@code status} for searching.
     * @return list of {@code orders} with the given {@code status}.
     * @throws DaoException if storage access error occurs.
     */
    List<Order> findByStatus(String status) throws DaoException;

    /**
     * Finds {@code orders} by user id.
     *
     * @param userId {@code id} of the user that made orders.
     * @return list of {@code orders} from the given user.
     * @throws DaoException if storage access error occurs.
     */
    List<Order> findByUserId(Long userId) throws DaoException;

    /**
     * Finds {@code orders} by created date in range
     * between {@code from} and {@code to}.
     *
     * @param from {@code from} creation datetime.
     * @param to   {@code to} creation datetime.
     * @return list of {@code orders} in the given range.
     * @throws DaoException if storage access error occurs.
     */
    List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to)
            throws DaoException;
}
