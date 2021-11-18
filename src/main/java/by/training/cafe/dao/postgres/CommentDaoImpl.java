package by.training.cafe.dao.postgres;

import by.training.cafe.dao.CommentDao;
import by.training.cafe.dao.DaoException;
import by.training.cafe.entity.Comment;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * The class {@code CommentDaoImpl} is a class that extends
 * {@link AbstractSqlDao} and implements {@link CommentDao}.<br/>
 * Provides access to the PostgreSQL database.
 *
 * @author Nikita Romanov
 * @see AbstractSqlDao
 * @see CommentDao
 */
public class CommentDaoImpl
        extends AbstractSqlDao<Long, Comment> implements CommentDao {

    private static final Logger log = LogManager.getLogger(CommentDaoImpl.class);

    private static final String ID_COLUMN_NAME = "id";
    private static final String USER_ID_COLUMN_NAME = "user_id";
    private static final String DISH_ID_COLUMN_NAME = "dish_id";
    private static final String RATING_COLUMN_NAME = "rating";
    private static final String BODY_COLUMN_NAME = "body";
    private static final String CREATED_AT_COLUMN_NAME = "created_at";

    private static final String FIND_ALL_SQL = """
            SELECT id, user_id, dish_id, rating, body, created_at
            FROM comment""";
    private static final String FIND_ALL_WITH_LIMIT_AND_OFFSET_SQL
            = FIND_ALL_SQL + LIMIT_SQL + OFFSET_SQL;
    private static final String FIND_BY_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "id = ?";
    private static final String FIND_BY_USER_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "user_id = ?";
    private static final String FIND_BY_DISH_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "dish_id = ?";
    private static final String CREATE_SQL = """
            INSERT INTO comment (user_id, dish_id, rating, body,created_at)
            VALUES (?, ?, ?, ?, ?::TIMESTAMP)""";
    private static final String UPDATE_SQL = """
            UPDATE comment
            SET user_id    = ?,
                dish_id    = ?,
                rating     = ?,
                body       = ?,
                created_at = ?::TIMESTAMP
            WHERE id = ?""";
    private static final String DELETE_SQL = """
            DELETE FROM comment
            WHERE id = ?""";
    private static final String COUNT_SQL = """
            SELECT count(id)
            FROM comment""";
    private static final String COUNT_GROUP_BY_RATING_SQL = """
            SELECT rating, count(id)
            FROM comment
            GROUP BY rating""";


    public CommentDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List<Comment> findAll() throws DaoException {
        List<Comment> comments = executeSelectQuery(
                FIND_ALL_SQL, Collections.emptyList());
        log.debug(RESULT_LOG_MESSAGE, comments);
        return comments;
    }

    @Override
    public List<Comment> findAll(Long limit, Long offset) throws DaoException {
        log.debug("Received limit = {}, offset = {}", limit, offset);
        List<Comment> comments = executeSelectQuery(
                FIND_ALL_WITH_LIMIT_AND_OFFSET_SQL, List.of(limit, offset));
        log.debug(RESULT_LOG_MESSAGE, comments);
        return comments;
    }

    @Override
    public Long count() throws DaoException {
        Long count = executeCountQuery(COUNT_SQL);
        log.debug("Count result: {}", count);
        return count;
    }

    @Override
    public Optional<Comment> findById(Long id) throws DaoException {
        log.debug("Received id: {}", id);
        List<Comment> list = executeSelectQuery(
                FIND_BY_ID_SQL, List.of(id));
        Optional<Comment> maybeComment = getFirstEntityFromList(list);
        log.debug(RESULT_LOG_MESSAGE, maybeComment);
        return maybeComment;
    }

    @Override
    public void create(Comment entity) throws DaoException {
        log.debug("Received comment: {}", entity);
        List<Object> params = createParamsList(entity);
        log.debug("{} params for query: {}", entity, params);
        Optional<Long> maybeId = executeCreateQuery(
                CREATE_SQL, Long.class, params);
        maybeId.ifPresentOrElse(
                entity::setId,
                () -> log.error("No generated keys for {}", entity)
        );
        log.debug("{} was created in db", entity);
    }

    @Override
    public boolean update(Comment entity) throws DaoException {
        log.debug("Received comment: {}", entity);
        List<Object> params = createParamsList(entity);
        params.add(entity.getId());
        log.debug("{} params for query: {}", entity, params);
        int updatedRows = executeUpdateQuery(UPDATE_SQL, params);
        boolean isUpdated = isOnlyOneRowUpdated(updatedRows);
        if (isUpdated) {
            log.debug("{} was updated", entity);
        } else {
            log.warn("{} wasn't updated", entity);
        }
        return isUpdated;
    }

    @Override
    public boolean delete(Long id) throws DaoException {
        log.debug("Received id: {}", id);
        int updatedRows = executeUpdateQuery(DELETE_SQL, List.of(id));
        boolean isDeleted = isOnlyOneRowUpdated(updatedRows);
        if (isDeleted) {
            log.debug("Comment with id {} was deleted", id);
        } else {
            log.warn("Comment with id {} wasn't deleted", id);
        }
        return isDeleted;
    }

    @Override
    public List<Comment> findByUserId(Long userId) throws DaoException {
        log.debug("Received userId = {}", userId);
        List<Comment> comments = executeSelectQuery(
                FIND_BY_USER_ID_SQL, List.of(userId));
        log.debug(RESULT_LOG_MESSAGE, comments);
        return comments;
    }

    @Override
    public List<Comment> findByDishId(Long dishId) throws DaoException {
        log.debug("Received dishId = {}", dishId);
        List<Comment> comments = executeSelectQuery(
                FIND_BY_DISH_ID_SQL, List.of(dishId));
        log.debug(RESULT_LOG_MESSAGE, comments);
        return comments;
    }

    @Override
    public Map<Short, Long> countGroupByRating() throws DaoException {
        try (PreparedStatement prepareStatement
                     = connection.prepareStatement(COUNT_GROUP_BY_RATING_SQL)) {
            ResultSet resultSet = prepareStatement.executeQuery();
            Map<Short, Long> result = new HashMap<>();
            while (resultSet.next()) {
                Short rating = resultSet.getObject(RATING_COLUMN_NAME, Short.class);
                Long count = resultSet.getObject(COUNT_COLUMN_NAME, Long.class);
                result.put(rating, count);
            }
            log.debug("Result map: {}", result);
            return result;
        } catch (SQLException e) {
            throw new DaoException(SQL_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    @Override
    protected Comment buildEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(
                ID_COLUMN_NAME, Long.class);
        log.trace("id = {}", id);

        Long userId = resultSet.getObject(
                USER_ID_COLUMN_NAME, Long.class);
        log.trace("userId = {}", id);
        User user = User.builder().id(userId).build();

        Long dishId = resultSet.getObject(
                DISH_ID_COLUMN_NAME, Long.class);
        log.trace("dishId = {}", id);
        Dish dish = Dish.builder().id(dishId).build();

        Short rating = resultSet.getObject(
                RATING_COLUMN_NAME, Short.class);
        log.trace("rating = {}", id);

        String body = resultSet.getObject(
                BODY_COLUMN_NAME, String.class);
        log.trace("body = {}", body);

        Timestamp createdAt = resultSet.getObject(
                CREATED_AT_COLUMN_NAME, Timestamp.class);
        log.trace("createdAt = {}", createdAt);

        return Comment.builder()
                .id(id)
                .user(user)
                .dish(dish)
                .rating(rating)
                .body(body)
                .createdAt(createdAt)
                .build();
    }

    private List<Object> createParamsList(Comment comment) {
        List<Object> params = new ArrayList<>();
        params.add(comment.getUser().getId());
        params.add(comment.getDish().getId());
        params.add(comment.getRating());
        params.add(comment.getBody());
        params.add(comment.getCreatedAt().toString());
        return params;
    }
}
