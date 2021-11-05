package by.training.cafe.dao;

import by.training.cafe.entity.Dish;

import java.util.List;

/**
 * The interface {@code DishDao} is an interface that
 * extends {@link BaseDao} and provides new operations with
 * {@link Dish} entity.
 *
 * @author Nikita Romanov
 * @see BaseDao
 * @see Dish
 */
public interface DishDao extends BaseDao<Long, Dish> {

    /**
     * Finds {@code dishes} by string in the {@code name}
     * or {@code description} (case insensitive).
     *
     * @param str string for searching.
     * @return list of found {@code dishes}.
     * @throws DaoException if storage access error occurs.
     */
    List<Dish> findByNameOrDescriptionLike(String str) throws DaoException;

    /**
     * Finds {@code dishes} by {@code category}.
     *
     * @param category {@code category} for searching.
     * @return list of {@code dishes} with the given {@code category}.
     * @throws DaoException if storage access error occurs.
     */
    List<Dish> findByCategory(String category) throws DaoException;
}
