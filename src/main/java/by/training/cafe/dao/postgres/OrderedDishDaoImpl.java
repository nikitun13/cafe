package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.OrderedDishDao;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderedDish;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The class {@code CommentDaoImpl} is a class that extends
 * {@link AbstractSqlDao} and implements {@link OrderedDishDao}.<br/>
 * Provides access to the PostgreSQL database.
 *
 * @author Nikita Romanov
 * @see AbstractSqlDao
 * @see OrderedDishDao
 */
public class OrderedDishDaoImpl
        extends AbstractSqlDao<SimpleEntry<Long, Long>, OrderedDish>
        implements OrderedDishDao {

    private static final Logger log
            = LogManager.getLogger(OrderedDishDaoImpl.class);

    private static final String ORDER_ID_COLUMN_NAME = "order_id";
    private static final String DISH_ID_COLUMN_NAME = "dish_id";
    private static final String DISH_COUNT_COLUMN_NAME = "dish_count";
    private static final String DISH_PRICE_COLUMN_NAME = "dish_price";

    private static final String FIND_ALL_SQL = """
            SELECT order_id, dish_id, dish_price, dish_count
            FROM dish_orders""";
    private static final String FIND_ALL_WITH_LIMIT_AND_OFFSET_SQL
            = FIND_ALL_SQL + LIMIT_SQL + OFFSET_SQL;
    private static final String FIND_BY_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "order_id = ?" + AND_SQL + "dish_id = ?";
    private static final String FIND_BY_ORDER_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "order_id = ?";
    private static final String CREATE_SQL = """
            INSERT INTO dish_orders (order_id, dish_id, dish_price, dish_count)
            VALUES (?, ?, ?, ?)""";
    private static final String UPDATE_SQL = """
            UPDATE dish_orders
            SET dish_price = ?,
                dish_count = ?
            WHERE order_id = ?
              AND dish_id = ?""";
    private static final String DELETE_SQL = """
            DELETE FROM dish_orders
            WHERE order_id = ?
              AND dish_id = ?""";
    private static final String COUNT_SQL = """
            SELECT count(order_id)
            FROM dish_orders""";

    public OrderedDishDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List<OrderedDish> findAll() throws DaoException {
        List<OrderedDish> orderedDishes = executeSelectQuery(
                FIND_ALL_SQL, Collections.emptyList());
        log.debug(RESULT_LOG_MESSAGE, orderedDishes);
        return orderedDishes;
    }

    @Override
    public List<OrderedDish> findAll(Long limit, Long offset) throws DaoException {
        log.debug("Received limit = {}, offset = {}", limit, offset);
        List<OrderedDish> orderedDishes = executeSelectQuery(
                FIND_ALL_WITH_LIMIT_AND_OFFSET_SQL, List.of(limit, offset));
        log.debug(RESULT_LOG_MESSAGE, orderedDishes);
        return orderedDishes;
    }

    @Override
    public Long count() throws DaoException {
        Long count = executeCountQuery(COUNT_SQL);
        log.debug("Count result: {}", count);
        return count;
    }

    @Override
    public Optional<OrderedDish> findById(SimpleEntry<Long, Long> entry)
            throws DaoException {
        log.debug("Received order id and dish id: {}", entry);
        List<OrderedDish> list = executeSelectQuery(
                FIND_BY_ID_SQL, List.of(entry.getKey(), entry.getValue()));
        Optional<OrderedDish> maybeOrderedDish = getFirstEntityFromList(list);
        log.debug(RESULT_LOG_MESSAGE, maybeOrderedDish);
        return maybeOrderedDish;
    }

    @Override
    public void create(OrderedDish entity) throws DaoException {
        log.debug("Received orderedDish: {}", entity);
        List<Object> params = List.of(
                entity.getOrder().getId(),
                entity.getDish().getId(),
                entity.getDishPrice(),
                entity.getDishCount());
        executeUpdateQuery(CREATE_SQL, params);
        log.debug("{} was created in db", entity);
    }

    @Override
    public boolean update(OrderedDish entity) throws DaoException {
        log.debug("Received orderedDish: {}", entity);
        List<Object> params = List.of(
                entity.getDishPrice(),
                entity.getDishCount(),
                entity.getOrder().getId(),
                entity.getDish().getId());
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
    public boolean delete(SimpleEntry<Long, Long> id) throws DaoException {
        log.debug("Received id: {}", id);
        int updatedRows = executeUpdateQuery(DELETE_SQL, List.of(id.getKey(), id.getValue()));
        boolean isDeleted = isOnlyOneRowUpdated(updatedRows);
        if (isDeleted) {
            log.debug("OrderedDish with id {} was deleted", id);
        } else {
            log.warn("OrderedDish with id {} wasn't deleted", id);
        }
        return isDeleted;
    }

    @Override
    public List<OrderedDish> findByOrderId(Long orderId) throws DaoException {
        log.debug("Received orderId = {}", orderId);
        List<OrderedDish> orderedDishes = executeSelectQuery(
                FIND_BY_ORDER_ID_SQL, List.of(orderId));
        log.debug(RESULT_LOG_MESSAGE, orderedDishes);
        return orderedDishes;
    }

    @Override
    protected OrderedDish buildEntity(ResultSet resultSet) throws SQLException {
        Long orderId = resultSet.getObject(ORDER_ID_COLUMN_NAME, Long.class);
        log.trace("orderId = {}", orderId);
        Order order = Order.builder().id(orderId).build();

        Long dishId = resultSet.getObject(DISH_ID_COLUMN_NAME, Long.class);
        log.trace("dishId = {}", dishId);
        Dish dish = Dish.builder().id(dishId).build();

        Long dishPrice = resultSet.getObject(DISH_PRICE_COLUMN_NAME, Long.class);
        log.trace("dishPrice = {}", dishPrice);

        Short dishCount = resultSet.getObject(DISH_COUNT_COLUMN_NAME, Short.class);
        log.trace("dishCount = {}", dishCount);

        return OrderedDish.builder()
                .order(order)
                .dish(dish)
                .dishPrice(dishPrice)
                .dishCount(dishCount)
                .build();
    }
}
