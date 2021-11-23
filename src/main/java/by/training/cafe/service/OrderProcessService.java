package by.training.cafe.service;

import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.dto.OrderedDishDto;

import java.util.List;

/**
 * The class {@code OrderService} is a class that
 * extends {@link Service}.<br/>
 * Provides business functionality of order processing.
 *
 * @author Nikita Romanov
 * @see Service
 */
public interface OrderProcessService extends Service {

    /**
     * Creates new {@code order} with the given {@code orderedDishes}.
     * Updates user points.
     *
     * @param createOrderDto createOrderDto for creation a new {@code order}.
     * @param orderedDishes  {@code orderedDishes} to be added
     *                       to the new {@code order}.
     * @throws ServiceException if {@code DaoException} occurred
     *                          or {@code createOrderDto}
     *                          or {@code orderedDishes}
     *                          is invalid.
     */
    void createOrder(CreateOrderDto createOrderDto,
                     List<OrderedDishDto> orderedDishes)
            throws ServiceException;
}
