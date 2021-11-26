package by.training.cafe.service;

import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.OrderedDishDto;

import java.time.Duration;
import java.util.List;
import java.util.Map;

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

    /**
     * Deletes a new {@code orderDto} if it was created
     * no later than 5 minutes ago and its status is {@code Pending}.
     *
     * @param orderDto order to be deleted.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or order can't be deleted
     *                          or {@code DaoException} occurred.
     */
    void deleteNewOrder(OrderDto orderDto) throws ServiceException;

    /**
     * Checks if the {@code orderDto} is deletable.<br/>
     * Deletable order is order that was created no later than
     * given {@code timeout} and its status is {@code Pending}.
     *
     * @param orderDto orderDto for check.
     * @param timeout  given timeout.
     * @return {@code true} if the {@code order} is deletable,
     * {@code false} otherwise.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or {@code timeout} is invalid.
     */
    boolean isDeletableOrder(OrderDto orderDto,
                             Duration timeout) throws ServiceException;

    /**
     * Creates map with order key and isDeletable status value.
     *
     * @param orders  orders for check.
     * @param timeout given timeout.
     * @return map of orders with their isDeletable status.
     * @throws ServiceException if {@code orders} is invalid
     *                          or {@code timeout} is invalid.
     * @see #isDeletableOrder(OrderDto, Duration)
     */
    Map<OrderDto, Boolean> isDeletableOrders(List<OrderDto> orders,
                                             Duration timeout)
            throws ServiceException;

    /**
     * Completes order.
     *
     * @param orderDto {@code order} to be completed.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or {@code DaoException} occurred.
     */
    void completeOrder(OrderDto orderDto) throws ServiceException;

    /**
     * Completes orders.
     *
     * @param orders {@code orders} to be completed.
     * @throws ServiceException if any {@code order} is invalid
     *                          or {@code DaoException} occurred.
     * @see #completeOrder(OrderDto)
     */
    void completeOrders(List<OrderDto> orders) throws ServiceException;

    /**
     * Cancels order.
     *
     * @param orderDto {@code order} to be canceled.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or {@code order} can't be canceled
     *                          or {@code DaoException} occurred.
     */
    void cancelOrder(OrderDto orderDto) throws ServiceException;

    /**
     * Cancels orders.
     *
     * @param orders {@code orders} to be canceled.
     * @throws ServiceException if any {@code order} is invalid
     *                          or any order can't be canceled
     *                          or {@code DaoException} occurred.
     * @see #cancelOrder(OrderDto)
     */
    void cancelOrders(List<OrderDto> orders) throws ServiceException;

    /**
     * Mark {@code order} as pending.
     *
     * @param orderDto {@code order} to be marked as pending.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or {@code order} can't be marked as pending
     *                          or {@code DaoException} occurred.
     */
    void toPendingOrder(OrderDto orderDto) throws ServiceException;

    /**
     * Mark orders as pending.
     *
     * @param orders {@code orders} to be marked as pending.
     * @throws ServiceException if any {@code order} is invalid
     *                          or any order can't be marked as pending
     *                          or {@code DaoException} occurred.
     * @see #toPendingOrder(OrderDto)
     */
    void toPendingOrders(List<OrderDto> orders) throws ServiceException;

    /**
     * Mark {@code order} as not collected.
     *
     * @param orderDto {@code order} to be marked as not collected.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or {@code order} can't be marked as not collected
     *                          or {@code DaoException} occurred.
     */
    void toNotCollectedOrder(OrderDto orderDto) throws ServiceException;

    /**
     * Mark orders as not collected.
     *
     * @param orders {@code orders} to be marked as pending.
     * @throws ServiceException if any {@code order} is invalid
     *                          or any order can't be marked as not collected
     *                          or {@code DaoException} occurred.
     * @see #toNotCollectedOrder(OrderDto)
     */
    void toNotCollectedOrders(List<OrderDto> orders) throws ServiceException;
}
