package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.pool.ConnectionPool;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderedDish;
import by.training.cafe.extension.DatabaseExtension;
import by.training.cafe.extension.SqlDaoParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({
        DatabaseExtension.class,
        SqlDaoParamResolver.class
})
class OrderedDishDaoImplTest {

    private static final String ORDER_ID_COLUMN_NAME = "order_id";
    private static final String DISH_ID_COLUMN_NAME = "dish_id";
    private static final String DISH_COUNT_COLUMN_NAME = "dish_count";
    private static final String DISH_PRICE_COLUMN_NAME = "dish_price";

    private static final String INSERT_INTO_ORDERED_DISH_SQL = """
            INSERT INTO dish_orders (order_id, dish_id, dish_price, dish_count)
            VALUES (3000000, 1000000, 2000, 3),
                   (3000000, 1000001, 2500, 2),
                   (3000003, 1000002, 200, 5)""";
    private static final String INSERT_INTO_DISH_SQL = """
            INSERT INTO dish (id, name, picture, category, price, description)
            VALUES (1000000, 'Four seasons', 'pictures/four-seasons.png', 'PIZZA', 2000, 'Really delicious pizza!'),
                   (1000001, 'Chicken BBQ','pictures/chicken-bbq.png', 'PIZZA', 2500, 'Pizza with chicken and sauce BBQ'),
                   (1000002, 'Coca-Cola 1L','pictures/coca-cola.png', 'DRINKS', 200, 'Soft drink with caffeine and plant extracts.')""";
    private static final String INSERT_INTO_USERS_SQL = """
            INSERT INTO users (id, email, password, role, first_name, last_name, phone, points, is_blocked, language)
            VALUES (1000000, 'ivan@gmail.com', '$2a$10$.IVAN.PASS.43WRAZ3Fnyx.C/6PveEHf6JGzGo9X2SQSwM5djXdrO', 'CLIENT', 'Ivan', 'Melnikov','+375251111111',30,TRUE,'RU'),
                   (1000001, 'petr@mail.ru', '$2a$10$.ПЕТР.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr1', 'CLIENT', 'Петр', 'Шариков','+375442222222',130,FALSE,'RU'),
                   (1000002, 'john@gmail.com', '$2a$10$.John.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr2', 'ADMIN', 'John', 'Henson','+375333333333',270,FALSE,'EN'),
                   (1000003, 'hans@gmail.com', '$2a$10$.Hans.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr3', 'CLIENT', 'Hans', 'Münz','+375254444444',0,FALSE,'DE')""";
    private static final String INSERT_INTO_ORDERS_SQL = """
            INSERT INTO orders (id, user_id, created_at, expected_retrieve_date, actual_retrieve_date, status, debited_points, accrued_points, total_price)
            VALUES (3000000, 1000000, '2021-11-04T15:35:36', '2021-11-04T17:35:36', '2021-11-04T17:27:36', 'COMPLETED', 100, 10, 3000),
                   (3000001, 1000000, '2021-11-04T17:52:19', '2021-11-05T15:52:19', NULL, 'CANCELED', 0, 0, 400),
                   (3000002, 1000003, '2021-11-05T15:35:36', '2021-11-06T15:35:36', NULL, 'PENDING', 250, 0, 4000),
                   (3000003, 1000001, '2021-11-06T10:12:36', '2021-11-06T15:12:36', NULL, 'PENDING', 90, 0, 2500)""";
    private static final String DELETE_ORDERS_SQL = "DELETE FROM orders";
    private static final String DELETE_USERS_SQL = "DELETE FROM users";
    private static final String DELETE_ORDERED_DISH_SQL = "DELETE FROM dish_orders";
    private static final String DELETE_DISH_SQL = "DELETE FROM dish";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM dish_orders WHERE order_id = ? AND dish_id = ?";
    private static final Connection CONNECTION = ConnectionPool.getInstance().getConnection();

    private static final OrderedDish FIRST_ORDERED_DISH;
    private static final OrderedDish SECOND_ORDERED_DISH;
    private static final OrderedDish THIRD_ORDERED_DISH;

    private final OrderedDish newOrderedDish;
    private final OrderedDish secondOrderedDishClone;

    private final OrderedDishDaoImpl orderedDishDao;

    static {
        FIRST_ORDERED_DISH = OrderedDish.builder()
                .order(Order.builder().id(3_000_000L).build())
                .dish(Dish.builder().id(1_000_000L).build())
                .dishPrice(2000L)
                .dishCount((short) 3)
                .build();

        SECOND_ORDERED_DISH = OrderedDish.builder()
                .order(Order.builder().id(3_000_000L).build())
                .dish(Dish.builder().id(1_000_001L).build())
                .dishPrice(2500L)
                .dishCount((short) 2)
                .build();

        THIRD_ORDERED_DISH = OrderedDish.builder()
                .order(Order.builder().id(3_000_003L).build())
                .dish(Dish.builder().id(1_000_002L).build())
                .dishPrice(200L)
                .dishCount((short) 5)
                .build();
    }

    OrderedDishDaoImplTest(OrderedDishDaoImpl orderedDishDao) {
        this.orderedDishDao = orderedDishDao;

        newOrderedDish = OrderedDish.builder()
                .order(Order.builder().id(3_000_002L).build())
                .dish(Dish.builder().id(1_000_001L).build())
                .dishPrice(3000L)
                .dishCount((short) 2)
                .build();

        secondOrderedDishClone = OrderedDish.builder()
                .order(Order.builder().id(3_000_000L).build())
                .dish(Dish.builder().id(1_000_001L).build())
                .dishPrice(2500L)
                .dishCount((short) 2)
                .build();
    }

    public static Stream<Arguments> dataForFindByOrderIdMethod() {
        return Stream.of(
                Arguments.of(3_000_000L, List.of(FIRST_ORDERED_DISH, SECOND_ORDERED_DISH)),
                Arguments.of(3_000_003L, List.of(THIRD_ORDERED_DISH)),
                Arguments.of(3_000_002L, Collections.emptyList()),
                Arguments.of(3_000_001L, Collections.emptyList())
        );
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Statement statement = CONNECTION.createStatement()) {
            statement.executeUpdate(INSERT_INTO_USERS_SQL);
            statement.executeUpdate(INSERT_INTO_ORDERS_SQL);
            statement.executeUpdate(INSERT_INTO_DISH_SQL);
            statement.executeUpdate(INSERT_INTO_ORDERED_DISH_SQL);
        }
    }

    @Test
    @Tag("findAll")
    void shouldReturnAllOrderedDishesFromDatabase() throws DaoException {
        List<OrderedDish> expected = List.of(FIRST_ORDERED_DISH, SECOND_ORDERED_DISH, THIRD_ORDERED_DISH);

        List<OrderedDish> actual = orderedDishDao.findAll();

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @Test
    @Tag("findById")
    void shouldReturnExistingOrderedDishByOrderIdAndDishId() throws DaoException {
        OrderedDish expected = FIRST_ORDERED_DISH;

        Optional<OrderedDish> optionalOrderedDish
                = orderedDishDao.findById(new SimpleEntry<>(3_000_000L, 1_000_000L));

        optionalOrderedDish.ifPresentOrElse(
                (actual) -> assertEquals(expected, actual, () -> "expected to get:" + expected),
                Assertions::fail
        );
    }

    @Test
    @Tag("findById")
    void shouldReturnEmptyOptionalIfThereIsNoSuchOrderId() throws DaoException {
        Optional<OrderedDish> actual = orderedDishDao.findById(new SimpleEntry<>(-1L, 1_000_000L));

        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("findById")
    void shouldReturnEmptyOptionalIfThereIsNoSuchDishId() throws DaoException {
        Optional<OrderedDish> actual = orderedDishDao.findById(new SimpleEntry<>(3_000_000L, -1L));

        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("create")
    void shouldCreateOrderedDishInDatabase() throws DaoException, SQLException {
        OrderedDish expected = newOrderedDish;

        orderedDishDao.create(expected);
        OrderedDish actual = findById(3_000_002L, 1000001L);

        assertEquals(expected, actual, () -> "must create: " + expected);
    }

    @Test
    @Tag("update")
    void shouldUpdateOrderedDishInDatabase() throws DaoException, SQLException {
        OrderedDish expected = secondOrderedDishClone;
        expected.setDishPrice(1000L);
        expected.setDishCount((short) 8);

        boolean isUpdated = orderedDishDao.update(expected);
        OrderedDish actual = findById(3_000_000L, 1_000_001L);
        assertAll(
                () -> assertTrue(isUpdated, "must return true if orderedDish was updated in the database"),
                () -> assertEquals(expected, actual, () -> "must update in database to: " + expected)
        );
    }

    @Test
    @Tag("update")
    void shouldReturnFalseIfOrderedDishWasNotUpdated() throws DaoException {
        secondOrderedDishClone.setOrder(Order.builder().id(-1L).build());
        secondOrderedDishClone.setDishPrice(1000L);
        secondOrderedDishClone.setDishCount((short) 8);
        boolean isUpdated = orderedDishDao.update(secondOrderedDishClone);

        assertFalse(isUpdated, "mustn't update any orderedDishes if no such order id");
    }

    @Test
    @Tag("delete")
    void shouldDeleteOrderedDishFromDatabaseByOrderIdAndDishId() throws DaoException, SQLException {
        OrderedDish expected = secondOrderedDishClone;

        boolean isDeleted = orderedDishDao.delete(
                new SimpleEntry<>(expected.getOrder().getId(), expected.getDish().getId()));
        OrderedDish actual = findById(3_000_000L, 1_000_001L);

        assertAll(
                () -> assertTrue(isDeleted, "must return true if orderedDish was deleted from the database"),
                () -> assertNull(actual, () -> "must delete %s from database".formatted(expected))
        );
    }

    @Test
    @Tag("delete")
    void shouldReturnFalseIfOrderedDishWasNotDeletedBecauseOfInvalidOrderId() throws DaoException {
        boolean isDeleted = orderedDishDao.delete(
                new SimpleEntry<>(-1L, 1_000_000L));

        assertFalse(isDeleted, "mustn't delete any orderDishes if no such order id");
    }

    @Test
    @Tag("delete")
    void shouldReturnFalseIfOrderedDishWasNotDeletedBecauseOfInvalidDishId() throws DaoException {
        boolean isDeleted = orderedDishDao.delete(
                new SimpleEntry<>(3_000_001L, -1L));

        assertFalse(isDeleted, "mustn't delete any orderDishes if no such dish id");
    }

    @ParameterizedTest
    @MethodSource("dataForFindByOrderIdMethod")
    @Tag("findByOrderId")
    void shouldReturnOrderedDishesByOrderId(Long orderId, List<OrderedDish> expected) throws DaoException {
        List<OrderedDish> actual = orderedDishDao.findByOrderId(orderId);

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = CONNECTION.createStatement()) {
            statement.executeUpdate(DELETE_ORDERED_DISH_SQL);
            statement.executeUpdate(DELETE_DISH_SQL);
            statement.executeUpdate(DELETE_ORDERS_SQL);
            statement.executeUpdate(DELETE_USERS_SQL);
        }
    }

    @AfterAll
    static void closeConnection() {
        try {
            CONNECTION.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private OrderedDish findById(Long orderId, Long dishId) throws SQLException {
        try (PreparedStatement statement = CONNECTION.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, orderId);
            statement.setObject(2, dishId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return OrderedDish.builder()
                        .order(Order.builder().id(resultSet.getObject(ORDER_ID_COLUMN_NAME, Long.class)).build())
                        .dish(Dish.builder().id(resultSet.getObject(DISH_ID_COLUMN_NAME, Long.class)).build())
                        .dishPrice(resultSet.getObject(DISH_PRICE_COLUMN_NAME, Long.class))
                        .dishCount(resultSet.getObject(DISH_COUNT_COLUMN_NAME, Short.class))
                        .build();
            }
        }
        return null;
    }
}
