package by.training.cafe.dao;

import by.training.cafe.entity.OrderedDish;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

/**
 * The interface {@code OrderedDishDao} is an interface that
 * extends {@link BaseDao} and provides new operations with
 * {@link OrderedDish} entity.
 *
 * @author Nikita Romanov
 * @see BaseDao
 * @see OrderedDish
 */
public interface OrderedDishDao
        extends BaseDao<SimpleEntry<Long, Long>, OrderedDish> {

    /**
     * Finds {@code orderedDishes} by order id.
     *
     * @param orderId {@code id} of the order with {@code orderedDishes}.
     * @return list of {@code orderedDishes} from the given {@code order}.
     * @throws DaoException if storage access error occurs.
     */
    List<OrderedDish> findByOrderId(Long orderId) throws DaoException;
}
