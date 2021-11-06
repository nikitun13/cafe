package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.OrderDao;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderStatus;
import by.training.cafe.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The class {@code CommentDaoImpl} is a class that extends
 * {@link AbstractSqlDao} and implements {@link OrderDao}.<br/>
 * Provides access to the PostgreSQL database.
 *
 * @author Nikita Romanov
 * @see AbstractSqlDao
 * @see OrderDao
 */
public class OrderDaoImpl
        extends AbstractSqlDao<Long, Order> implements OrderDao {

    private static final Logger log = LogManager.getLogger(OrderDaoImpl.class);

    private static final String ID_COLUMN_NAME = "id";
    private static final String USER_ID_COLUMN_NAME = "user_id";
    private static final String CREATED_AT_COLUMN_NAME = "created_at";
    private static final String EXPECTED_RETRIEVE_DATE_COLUMN_NAME = "expected_retrieve_date";
    private static final String ACTUAL_RETRIEVE_DATE_COLUMN_NAME = "actual_retrieve_date";
    private static final String STATUS_COLUMN_NAME = "status";
    private static final String DEBITED_POINTS_COLUMN_NAME = "debited_points";
    private static final String ACCRUED_POINTS_COLUMN_NAME = "accrued_points";
    private static final String TOTAL_PRICE_COLUMN_NAME = "total_price";

    private static final String FIND_ALL_SQL = """
            SELECT id, user_id, created_at, expected_retrieve_date, actual_retrieve_date,
            status, debited_points, accrued_points, total_price
            FROM orders""";
    private static final String FIND_BY_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "id = ?";
    private static final String FIND_BY_USER_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "user_id = ?";
    private static final String FIND_BY_STATUS_SQL
            = FIND_ALL_SQL + WHERE_SQL + "status = ?::order_status";
    private static final String FIND_BY_CREATED_AT_BETWEEN_SQL
            = FIND_ALL_SQL + WHERE_SQL + "created_at BETWEEN ?::TIMESTAMP AND ?::TIMESTAMP";
    private static final String CREATE_SQL = """
            INSERT INTO orders (user_id, created_at, expected_retrieve_date,
            actual_retrieve_date, status, debited_points, accrued_points, total_price)
            VALUES (?, ?::TIMESTAMP, ?::TIMESTAMP, ?::TIMESTAMP, ?::order_status, ?, ?, ?)""";
    private static final String UPDATE_SQL = """
            UPDATE orders
            SET user_id                = ?,
                created_at             = ?::TIMESTAMP,
                expected_retrieve_date = ?::TIMESTAMP,
                actual_retrieve_date   = ?::TIMESTAMP,
                status                 = ?::order_status,
                debited_points         = ?,
                accrued_points         = ?,
                total_price            = ?
            WHERE id = ?""";
    private static final String DELETE_SQL = """
            DELETE FROM orders
            WHERE id = ?""";

    public OrderDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List<Order> findAll() throws DaoException {
        List<Order> orders = executeSelectQuery(
                FIND_ALL_SQL, Collections.emptyList());
        log.debug(RESULT_LOG_MESSAGE, orders);
        return orders;
    }

    @Override
    public Optional<Order> findById(Long id) throws DaoException {
        log.debug("Received id: {}", id);
        List<Order> list = executeSelectQuery(
                FIND_BY_ID_SQL, List.of(id));
        Optional<Order> maybeOrder = getFirstEntityFromList(list);
        log.debug(RESULT_LOG_MESSAGE, maybeOrder);
        return maybeOrder;
    }

    @Override
    public void create(Order entity) throws DaoException {
        log.debug("Received order: {}", entity);
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
    public boolean update(Order entity) throws DaoException {
        log.debug("Received order: {}", entity);
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
            log.debug("Order with id {} was deleted", id);
        } else {
            log.warn("Order with id {} wasn't deleted", id);
        }
        return isDeleted;
    }

    @Override
    public List<Order> findByStatus(String status) throws DaoException {
        log.debug("Received status = {}", status);
        List<Order> orders = executeSelectQuery(
                FIND_BY_STATUS_SQL, List.of(status));
        log.debug(RESULT_LOG_MESSAGE, orders);
        return orders;
    }

    @Override
    public List<Order> findByUserId(Long userId) throws DaoException {
        log.debug("Received userId = {}", userId);
        List<Order> orders = executeSelectQuery(
                FIND_BY_USER_ID_SQL, List.of(userId));
        log.debug(RESULT_LOG_MESSAGE, orders);
        return orders;
    }

    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime from,
                                              LocalDateTime to)
            throws DaoException {
        log.debug("Received from date = {} and to date = {}", from, to);
        List<Order> orders = executeSelectQuery(
                FIND_BY_CREATED_AT_BETWEEN_SQL, List.of(from, to));
        log.debug(RESULT_LOG_MESSAGE, orders);
        return orders;
    }

    @Override
    protected Order buildEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(ID_COLUMN_NAME, Long.class);
        log.trace("id = {}", id);

        Long userId = resultSet.getObject(USER_ID_COLUMN_NAME, Long.class);
        log.trace("userId = {}", userId);
        User user = User.builder().id(userId).build();

        LocalDateTime createdAt = resultSet.getObject(
                CREATED_AT_COLUMN_NAME, Timestamp.class).toLocalDateTime();
        log.trace("createdAt = {}", createdAt);

        LocalDateTime expectedRetrieveDate = resultSet.getObject(
                EXPECTED_RETRIEVE_DATE_COLUMN_NAME, Timestamp.class).toLocalDateTime();
        log.trace("expectedRetrieveDate = {}", expectedRetrieveDate);

        LocalDateTime actualRetrieveDate;
        Timestamp timestamp = resultSet.getObject(
                ACTUAL_RETRIEVE_DATE_COLUMN_NAME, Timestamp.class);
        actualRetrieveDate = timestamp != null
                ? timestamp.toLocalDateTime()
                : null;
        log.trace("actualRetrieveDate = {}", actualRetrieveDate);

        OrderStatus status = OrderStatus.valueOf(resultSet.getObject(
                STATUS_COLUMN_NAME, String.class));
        log.trace("status = {}", status);

        Long debitedPoints = resultSet.getObject(
                DEBITED_POINTS_COLUMN_NAME, Long.class);
        log.trace("debitedPoints = {}", debitedPoints);

        Long accruedPoints = resultSet.getObject(
                ACCRUED_POINTS_COLUMN_NAME, Long.class);
        log.trace("accruedPoints = {}", accruedPoints);

        Long totalPrice = resultSet.getObject(
                TOTAL_PRICE_COLUMN_NAME, Long.class);
        log.trace("totalPrice = {}", totalPrice);

        return Order.builder()
                .id(id)
                .user(user)
                .createdAt(createdAt)
                .expectedRetrieveDate(expectedRetrieveDate)
                .actualRetrieveDate(actualRetrieveDate)
                .status(status)
                .debitedPoints(debitedPoints)
                .accruedPoints(accruedPoints)
                .totalPrice(totalPrice)
                .build();
    }

    private List<Object> createParamsList(Order order) {
        List<Object> params = new ArrayList<>();
        params.add(order.getUser().getId());
        params.add(order.getCreatedAt().toString());
        params.add(order.getExpectedRetrieveDate().toString());
        LocalDateTime actualRetrieveDate = order.getActualRetrieveDate();
        String actualRetrieveParam = actualRetrieveDate != null
                ? actualRetrieveDate.toString()
                : null;
        params.add(actualRetrieveParam);
        params.add(order.getStatus().toString());
        params.add(order.getDebitedPoints());
        params.add(order.getAccruedPoints());
        params.add(order.getTotalPrice());
        return params;
    }
}