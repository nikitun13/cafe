package by.training.cafe.service;

import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.OrderedDishDto;

import java.util.List;
import java.util.Optional;

/**
 * The class {@code OrderedDishService} is a class that
 * implements {@link Service}.<br/>
 * Provides different business logic with {@code OrderedDish} entities
 * using {@link OrderedDishDto}.
 *
 * @author Nikita Romanov
 * @see Service
 * @see OrderedDishDto
 */
public interface OrderedDishService extends Service {

    /**
     * Returns all entities mapped to {@link OrderedDishDto}
     * from storage.
     *
     * @return all entities mapped to {@link OrderedDishDto}.
     * @throws ServiceException if DaoException occurred.
     */
    List<OrderedDishDto> findAll() throws ServiceException;

    /**
     * Finds entity by {@code order id} and {@code dish id}
     * and maps it to {@link OrderedDishDto}.<br/>
     *
     * @param orderId {@code id} of the {@code order}.
     * @param dishId  {@code id} of the {@code dish}.
     * @return optional {@code orderedDishDto}. If entity was found
     * optional contains {@link OrderedDishDto}, otherwise empty optional.
     * @throws ServiceException if {@code id} is invalid
     *                          or DaoException occurred.
     */
    Optional<OrderedDishDto> findById(Long orderId, Long dishId)
            throws ServiceException;

    /**
     * Creates new {@code OrderedDish} in the storage
     * using {@link OrderedDishDto}.
     *
     * @param orderedDishDto to be mapped to {@code OrderedDish}
     *                       and created in the storage.
     * @throws ServiceException if {@code orderedDishDto} is invalid
     *                          or DaoException occurred.
     */
    void create(OrderedDishDto orderedDishDto) throws ServiceException;

    /**
     * Updates {@code OrderedDish} in the storage using {@link OrderedDishDto}.
     *
     * @param orderedDishDto to be mapped to {@code OrderedDish}
     *                       and updated in the storage.
     * @return {@code true} if {@code orderedDish} was updated successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code orderedDishDto} is invalid
     *                          or DaoException occurred.
     */
    boolean update(OrderedDishDto orderedDishDto) throws ServiceException;

    /**
     * Deletes {@code OrderedDish} in the storage using {@link OrderedDishDto}.
     *
     * @param orderedDishDto to be mapped to {@code OrderedDish}
     *                       and deleted in the storage.
     * @return {@code true} if {@code orderedDish} was deleted successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code orderedDishDto} is invalid
     *                          or DaoException occurred.
     */
    boolean delete(OrderedDishDto orderedDishDto) throws ServiceException;

    /**
     * Finds {@code OrderedDishes} by {@code orderDto}
     * and maps them to {@link OrderedDishDto}.
     *
     * @param orderDto {@code orderDto} for search.
     * @return list of {@link OrderedDishDto} for the given {@code orderDto}.
     * @throws ServiceException if {@code orderDto} is invalid
     *                          or DaoException occurred.
     */
    List<OrderedDishDto> findByOrderDto(OrderDto orderDto)
            throws ServiceException;
}
