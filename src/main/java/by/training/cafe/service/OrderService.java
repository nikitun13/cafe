package by.training.cafe.service;

import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.UserDto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * The class {@code OrderService} is a class that
 * extends {@link Service}.<br/>
 * Provides different business logic with {@code order} entities
 * using {@link OrderDto} and {@link CreateOrderDto}.
 *
 * @author Nikita Romanov
 * @see Service
 * @see OrderDto
 * @see CreateOrderDto
 */
public interface OrderService extends Service {

    /**
     * Returns all {@code orders} mapped to {@link OrderDto}
     * from the storage.
     *
     * @return all entities mapped to {@link OrderDto}.
     * @throws ServiceException if DaoException occurred.
     */
    List<OrderDto> findAll() throws ServiceException;

    /**
     * Finds {@code order} by {@code id} and maps it to {@link OrderDto}.
     *
     * @param id entity {@code id}.
     * @return optional {@code orderDto}. If entity was found
     * optional contains {@link OrderDto}, otherwise empty optional.
     * @throws ServiceException if {@code id} is invalid
     *                          or DaoException occurred.
     */
    Optional<OrderDto> findById(Long id) throws ServiceException;

    /**
     * Creates new {@code order} in the storage using {@link CreateOrderDto}.
     *
     * @param createOrderDto to be mapped to {@code order}
     *                       and created in the storage.
     * @return {@link OrderDto} of new created {@code order}.
     * @throws ServiceException if {@code createOrderDto} is invalid
     *                          or DaoException occurred.
     */
    OrderDto create(CreateOrderDto createOrderDto) throws ServiceException;

    /**
     * Updates {@code order} in the storage using {@link OrderDto}.
     *
     * @param orderDto to be mapped to {@code order} and updated in the storage.
     * @return {@code true} if {@code order} was updated successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or DaoException occurred.
     */
    boolean update(OrderDto orderDto) throws ServiceException;

    /**
     * Deletes {@code order} in the storage using {@link OrderDto}.
     *
     * @param orderDto to be mapped to {@code order} and deleted in the storage.
     * @return {@code true} if {@code order} was deleted successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or DaoException occurred.
     */
    boolean delete(OrderDto orderDto) throws ServiceException;

    /**
     * Finds {@code orders} by {@code userDto}
     * and maps them to {@link OrderDto}.
     *
     * @param userDto {@code userDto} that made orders.
     * @return list of {@link OrderDto} for the given user.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or DaoException occurred.
     */
    List<OrderDto> findByUserDto(UserDto userDto) throws ServiceException;

    /**
     * Finds {@code orders} by created date
     * in range between {@code from} and {@code to}
     * and maps them to {@link UserDto}.
     *
     * @param from {@code from} creation datetime.
     * @param to   {@code to} creation datetime.
     * @return list of {@code orders} in the given range.
     * @throws ServiceException {@code from} or {@code to} is invalid
     *                          or DaoException occurred.
     */
    List<OrderDto> findByCreatedAtBetween(Timestamp from, Timestamp to)
            throws ServiceException;
}
