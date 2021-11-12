package by.training.cafe.service;

import by.training.cafe.dto.DishDto;
import by.training.cafe.entity.Dish;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The class {@code DishService} is a class that
 * implements {@link Service}.<br/>
 * Provides different business logic with {@link Dish} entities.
 *
 * @author Nikita Romanov
 * @see Service
 * @see Dish
 * @see DishDto
 */
public interface DishService extends Service {

    /**
     * Returns all entities mapped to {@link DishDto}
     * from storage.
     *
     * @return all entities mapped to {@link DishDto}.
     * @throws ServiceException if DaoException occurred.
     */
    List<DishDto> findAll() throws ServiceException;

    /**
     * Finds entity by {@code id} and maps it to {@link DishDto}.
     *
     * @param id entity {@code id}.
     * @return optional {@code dishDto}. If entity was found
     * optional contains {@code dishDao}, otherwise empty optional.
     * @throws ServiceException if {@code id} is invalid
     *                          or DaoException occurred.
     */
    Optional<DishDto> findById(Long id) throws ServiceException;

    /**
     * Creates new {@link Dish} in the storage using {@link DishDto}.
     *
     * @param dishDto to be mapped to {@link Dish} and created in the storage.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    void create(DishDto dishDto) throws ServiceException;

    /**
     * Updates {@link Dish} in the storage using {@link DishDto}.
     *
     * @param dishDto to be mapped to {@link Dish} and updated in the storage.
     * @return {@code true} if {@code dish} was updated successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    boolean update(DishDto dishDto) throws ServiceException;

    /**
     * Deletes {@link Dish} in the storage using {@link DishDto}.
     *
     * @param dishDto to be mapped to {@link Dish} and deleted in the storage.
     * @return {@code true} if {@code dish} was deleted successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    boolean delete(DishDto dishDto) throws ServiceException;

    /**
     * Returns all {@link Dish} mapped to {@link DishDto}
     * and groups them by category.
     *
     * @return Map of Category vs List of {@link DishDto}.
     * @throws ServiceException if DaoException occurred.
     */
    Map<String, List<DishDto>> findAllGroupByCategory() throws ServiceException;

    /**
     * Returns all {@link Dish} mapped to {@link DishDto}
     * that contains in name or description given {@code str}.
     * It is possible to use more than one word in the {@code str}.
     *
     * @param str string for searching.
     * @return list of {@link DishDto}.
     * @throws ServiceException if DaoException occurred
     *                          or {@code str} is invalid.
     */
    List<DishDto> findByNameOrDescriptionLike(String str)
            throws ServiceException;

    /**
     * Returns all {@link Dish} mapped to {@link DishDto}
     * that have given {@code category}.
     *
     * @param category dish category.
     * @return list of {@link DishDto}.
     * @throws ServiceException if DaoException occurred
     *                          or {@code category} is invalid.
     */
    List<DishDto> findByCategory(String category) throws ServiceException;
}
