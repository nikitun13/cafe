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
     * Finds {@code comments} by user id.
     *
     * @param userId {@code id} of the user that leave comments.
     * @return list of {@code comments} from the given user.
     * @throws DaoException if storage access error occurs.
     */
    List<Comment> findByUserId(Long userId) throws DaoException;

    /**
     * Finds {@code comments} by dish id.
     *
     * @param dishId {@code id} of the dish,
     *               which comments are needed to be returned.
     * @return list of {@code comments} to the given dish.
     * @throws DaoException if storage access error occurs.
     */
    List<Comment> findByDishId(Long dishId) throws DaoException;

    /**
     * Counts all comments grouped by rating.
     *
     * @return map where key is rating, value is counted entities.
     * @throws DaoException if storage access error occurs.
     */
    Map<Short, Long> countGroupByRating() throws DaoException;
}
