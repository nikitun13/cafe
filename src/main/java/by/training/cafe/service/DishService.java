package by.training.cafe.service;

import by.training.cafe.dto.DishDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The class {@code DishService} is a class that
 * implements {@link Service}.<br/>
 * Provides different business logic with {@code Dish} entities.
 *
 * @author Nikita Romanov
 * @see Service
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
     * optional contains {@link DishDto}, otherwise empty optional.
     * @throws ServiceException if {@code id} is invalid
     *                          or DaoException occurred.
     */
    Optional<DishDto> findById(Long id) throws ServiceException;

    /**
     * Creates new {@code Dish} in the storage using {@link DishDto}.
     *
     * @param dishDto to be mapped to {@code Dish} and created in the storage.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    void create(DishDto dishDto) throws ServiceException;

    /**
     * Updates {@code Dish} in the storage using {@link DishDto}.
     *
     * @param dishDto to be mapped to {@code Dish} and updated in the storage.
     * @return {@code true} if {@code dish} was updated successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    boolean update(DishDto dishDto) throws ServiceException;

    /**
     * Deletes {@code Dish} in the storage using {@link DishDto}.
     *
     * @param dishDto to be mapped to {@code Dish} and deleted in the storage.
     * @return {@code true} if {@code dish} was deleted successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    boolean delete(DishDto dishDto) throws ServiceException;

    /**
     * Returns all {@code Dish} mapped to {@link DishDto}
     * and groups them by category.
     *
     * @return Map of Category vs List of {@link DishDto}.
     * @throws ServiceException if DaoException occurred.
     */
    Map<String, List<DishDto>> findAllGroupByCategory() throws ServiceException;

    /**
     * Returns all {@code Dish} mapped to {@link DishDto}
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
     * Returns all {@code Dish} mapped to {@link DishDto}
     * that have given {@code category}.
     *
     * @param category dish category.
     * @return list of {@link DishDto}.
     * @throws ServiceException if DaoException occurred
     *                          or {@code category} is invalid.
     */
    List<DishDto> findByCategory(String category) throws ServiceException;
}
