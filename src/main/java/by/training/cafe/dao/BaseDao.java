package by.training.cafe.dao;

import by.training.cafe.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Describes the interface of the generic {@code dao}, which provides
 * basic CRUD (Create, Reed, Update, Delete) operations.
 *
 * @param <K> id (key) of the entity.
 * @param <E> entity itself.
 * @author Nikita Romanov
 */
public interface BaseDao<K, E extends Entity> {

    /**
     * Finds all {@code entities}  from the storage.
     *
     * @return list of all {@code entities} from the storage.
     * @throws DaoException if storage access error occurs.
     */
    List<E> findAll() throws DaoException;

    /**
     * Finds {@code entity} by its {@code id}.
     *
     * @param id {@code id} of the {@code entity}.
     * @return Optional {@code entity}. If the given {@code id} exists,
     * {@link Optional} contains corresponding entity,
     * empty {@link Optional} otherwise.
     * @throws DaoException if storage access error occurs.
     */
    Optional<E> findById(K id) throws DaoException;

    /**
     * Adds new {@code entity} to the storage.
     *
     * @param entity {@code entity} to be added to the storage.
     * @throws DaoException if storage access error occurs
     *                      or some creation constraints have occurred.
     */
    void create(E entity) throws DaoException;

    /**
     * Updates {@code entity} in the storage.
     *
     * @param entity {@code entity} to be updated in the storage.
     * @return {@code true} if {@code entity} was updated,
     * {@code false} otherwise.
     * @throws DaoException if storage access error occurs
     *                      or some update constraints have occurred.
     */
    boolean update(E entity) throws DaoException;

    /**
     * Deletes {@code entity} from the storage by its {@code id}.
     *
     * @param id of the {@code entity} to be deleted.
     * @return {@code true} if {@code entity} was deleted,
     * {@code false} otherwise.
     * @throws DaoException if storage access error occurs
     *                      or some delete constraints have occurred.
     */
    boolean delete(K id) throws DaoException;
}
