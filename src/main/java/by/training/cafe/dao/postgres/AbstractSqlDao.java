package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

/**
 * The class {@code AbstractSqlDao} is an abstract class that
 * realize basic executions of {@code SQL} queries.<br/>
 * {@code SQL} queries are provided by inherited classes.
 *
 * @param <K> id (key) of the entity.
 * @param <E> entity itself.
 * @author Nikita Romanov
 */
public abstract class AbstractSqlDao<K, E> {

    private static final Logger log = LogManager.getLogger(AbstractSqlDao.class);

    protected static final String SQL_EXCEPTION_OCCURRED_MESSAGE
            = "SQLException occurred";
    protected static final String RESULT_LOG_MESSAGE = "Result: {}";
    protected static final String EXECUTING_SQL_LOG_MESSAGE = "Executing SQL: {}";

    protected static final String PERCENT = "%";
    protected static final String AND_SQL = " AND ";
    protected static final String OR_SQL = " OR ";
    protected static final String WHERE_SQL = " WHERE ";

    protected final Connection connection;

    protected AbstractSqlDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes {@code SQL} queries with returning {@link ResultSet}
     * using {@link PreparedStatement} and given {@code params}.
     *
     * @param sql    {@code SQL} query to be executed.
     * @param params params for this {@code SQL} query.
     * @return list of found {@code entities}.
     * @throws DaoException if SQLException occurred.
     */
    protected List<E> executeSelectQuery(String sql, List<Object> params)
            throws DaoException {
        try (PreparedStatement prepareStatement
                     = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); ++i) {
                prepareStatement.setObject(i + 1, params.get(i));
            }
            log.debug(EXECUTING_SQL_LOG_MESSAGE, prepareStatement);
            ResultSet resultSet = prepareStatement.executeQuery();
            List<E> entities = new ArrayList<>();
            while (resultSet.next()) {
                entities.add(buildEntity(resultSet));
            }
            return entities;
        } catch (SQLException e) {
            throw new DaoException(SQL_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    /**
     * Executes {@code SQL} queries without returning {@link ResultSet}
     * using {@link PreparedStatement} and given {@code params}.
     *
     * @param sql    {@code SQL} query to be executed.
     * @param params params for this {@code SQL} query.
     * @return returns updated rows count.
     * @throws DaoException if SQLException occurred.
     */
    protected int executeUpdateQuery(String sql, List<Object> params)
            throws DaoException {
        try (PreparedStatement prepareStatement
                     = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); ++i) {
                prepareStatement.setObject(i + 1, params.get(i));
            }
            log.debug(EXECUTING_SQL_LOG_MESSAGE, prepareStatement);
            prepareStatement.executeUpdate();
            int updateCount = prepareStatement.getUpdateCount();
            log.debug("updated rows: {}", updateCount);
            return updateCount;
        } catch (SQLException e) {
            throw new DaoException(SQL_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    /**
     * Executes {@code SQL} queries without returning {@link ResultSet}
     * but with returning {@code Generated keys}
     * using {@link PreparedStatement} and given {@code params}.
     *
     * @param sql      {@code SQL} query to be executed.
     * @param keyClass generated key (id) class.
     * @param params   params for this {@code SQL} query.
     * @return Optional of the Generated key.
     * If the {@link ResultSet} contains generated key,
     * {@link Optional} contains that key, empty {@link Optional} otherwise.
     * @throws DaoException if SQLException occurred.
     */
    protected Optional<K> executeCreateQuery(String sql,
                                             Class<K> keyClass,
                                             List<Object> params)
            throws DaoException {
        try (PreparedStatement prepareStatement
                     = connection.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.size(); ++i) {
                prepareStatement.setObject(i + 1, params.get(i));
            }
            log.debug(EXECUTING_SQL_LOG_MESSAGE, prepareStatement);
            prepareStatement.executeUpdate();
            ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return Optional.ofNullable(generatedKeys.getObject(1, keyClass));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(SQL_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    /**
     * It is used when 0 or 1 {@code entity} should
     * have been returned as a result of a {@code SQL} query.
     *
     * @param entities list of the {@code SQL} query execution result.
     * @return Optional {@code entity}. If the list is not empty,
     * {@link Optional} contains first {@code entity} from the list,
     * empty {@link Optional} otherwise.
     */
    protected Optional<E> getFirstEntityFromList(List<E> entities) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(entities.get(0));
        }
    }

    /**
     * Checks that there is only one updated row.<br/>
     * Warns if there are more than one updated row.
     *
     * @param updatedRowCount number of updated rows.
     * @return {@code true} is there is only one updated row,
     * {@code false} otherwise.
     */
    protected boolean isOnlyOneRowUpdated(int updatedRowCount) {
        if (updatedRowCount > 1) {
            log.warn("More than 1 updated rows. Updated rows = {}",
                    updatedRowCount);
        }
        return updatedRowCount == 1;
    }

    /**
     * Builds {@code entity} from the {@link ResultSet}.
     * Implementation depends on the specific SQL Dao.
     *
     * @param resultSet {@link ResultSet} with {@code entity}'s data.
     * @return built {@code entity}.
     * @throws SQLException if error occurs during
     *                      entity building from the {@link ResultSet}.
     */
    protected abstract E buildEntity(ResultSet resultSet) throws SQLException;
}
