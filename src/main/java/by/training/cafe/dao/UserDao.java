package by.training.cafe.dao;

import by.training.cafe.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * The interface {@code UserDao} is an interface that
 * extends {@link BaseDao} and provides new operations with
 * {@link User} entity.
 *
 * @author Nikita Romanov
 * @see BaseDao
 * @see User
 */
public interface UserDao extends BaseDao<Long, User> {

    /**
     * Finds {@link User} by {@code phone}.
     *
     * @param phone {@code phone} of the user.
     * @return Optional {@code user}. If the given {@code phone} exists,
     * {@link Optional} contains corresponding {@code user},
     * empty {@link Optional} otherwise.
     * @throws DaoException if storage access error occurs.
     */
    Optional<User> findByPhone(String phone) throws DaoException;

    /**
     * Finds {@link User} by {@code email}.
     *
     * @param email {@code email} of the user.
     * @return Optional {@code user}. If the given {@code email} exists,
     * {@link Optional} contains corresponding {@code user},
     * empty {@link Optional} otherwise.
     * @throws DaoException if storage access error occurs.
     */
    Optional<User> findByEmail(String email) throws DaoException;

    /**
     * Finds {@code users} by {@code role}.
     *
     * @param role {@code role} of the {@code users} for search.
     * @return list of {@code users} with the given {@code role}.
     * @throws DaoException if storage access error occurs.
     */
    List<User> findByRole(String role) throws DaoException;
}
