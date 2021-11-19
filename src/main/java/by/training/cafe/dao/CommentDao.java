package by.training.cafe.dao;

import by.training.cafe.entity.Comment;

import java.util.List;
import java.util.Map;

/**
 * The interface {@code CommentDao} is an interface that
 * extends {@link BaseDao} and provides new operations with
 * {@link Comment} entity.
 *
 * @author Nikita Romanov
 * @see BaseDao
 * @see Comment
 */
public interface CommentDao extends BaseDao<Long, Comment> {

    /**
     * Finds {@code comments} by user id and orders them by
     * {@code createdAt} in descending order.
     *
     * @param userId {@code id} of the user that leave comments.
     * @return list of {@code comments} from the given user.
     * @throws DaoException if storage access error occurs.
     */
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId)
            throws DaoException;

    /**
     * Finds {@code comments} by dish id and orders them by
     * {@code createdAt} in descending order.
     *
     * @param dishId {@code id} of the dish,
     *               which comments are needed to be returned.
     * @return list of {@code comments} to the given dish.
     * @throws DaoException if storage access error occurs.
     */
    List<Comment> findByDishIdOrderByCreatedAtDesc(Long dishId)
            throws DaoException;

    /**
     * Finds {@code comments} with given {@code limit} and {@code offset}
     * by dish id and orders them by {@code createdAt} in descending order.
     *
     * @param dishId {@code id} of the dish,
     *               which comments are needed to be returned.
     * @param limit  number of returning entities.
     * @param offset offset in the storage.
     * @return list of {@code comments} to the given dish.
     * @throws DaoException if storage access error occurs.
     */
    List<Comment> findByDishIdOrderByCreatedAtDesc(Long dishId,
                                                   Long limit,
                                                   Long offset)
            throws DaoException;

    /**
     * Counts all comments grouped by rating.
     *
     * @param dishId {@code id} of the dish.
     * @return map where key is rating, value is counted entities.
     * @throws DaoException if storage access error occurs.
     */
    Map<Short, Long> countCommentsByDishIdGroupByRating(Long dishId)
            throws DaoException;

    /**
     * Calculates average dish rating.
     *
     * @param dishId {@code id} of the dish,
     *               which average rating is needed to be count.
     * @return average dish rating.
     * @throws DaoException if storage access error occurs.
     */
    Double averageDishRating(Long dishId) throws DaoException;

    /**
     * Counts number of entities in the storage with the
     * given {@code dishId}.
     *
     * @param dishId id of the {@code dish}.
     * @return number of entities with the given {@code dishId}.
     * @throws DaoException if storage access error occurs.
     */
    Long countByDishId(Long dishId) throws DaoException;
}
