package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.pool.ConnectionPool;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderStatus;
import by.training.cafe.entity.User;
import by.training.cafe.extension.DatabaseExtension;
import by.training.cafe.extension.SqlDaoParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({
        DatabaseExtension.class,
        SqlDaoParamResolver.class
})
class OrderDaoImplTest {

    private static final String ID_COLUMN_NAME = "id";
    private static final String USER_ID_COLUMN_NAME = "user_id";
    private static final String CREATED_AT_COLUMN_NAME = "created_at";
    private static final String EXPECTED_RETRIEVE_DATE_COLUMN_NAME = "expected_retrieve_date";
    private static final String ACTUAL_RETRIEVE_DATE_COLUMN_NAME = "actual_retrieve_date";
    private static final String STATUS_COLUMN_NAME = "status";
    private static final String DEBITED_POINTS_COLUMN_NAME = "debited_points";
    private static final String ACCRUED_POINTS_COLUMN_NAME = "accrued_points";
    private static final String TOTAL_PRICE_COLUMN_NAME = "total_price";

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
    private static final String FIND_BY_ID_SQL = "SELECT * FROM orders WHERE id = ?";
    private static final Connection connection = ConnectionPool.getInstance().getConnection();

    private static final Order IVAN_FIRST_ORDER;
    private static final Order IVAN_SECOND_ORDER;
    private static final Order HANS_ORDER;
    private static final Order PETR_ORDER;

    static {
        IVAN_FIRST_ORDER = Order.builder()
                .id(3000000L)
                .user(User.builder().id(1000000L).build())
                .createdAt(LocalDateTime.parse("2021-11-04T15:35:36"))
                .expectedRetrieveDate(LocalDateTime.parse("2021-11-04T17:35:36"))
                .actualRetrieveDate(LocalDateTime.parse("2021-11-04T17:27:36"))
                .status(OrderStatus.COMPLETED)
                .accruedPoints(10L)
                .debitedPoints(100L)
                .totalPrice(3000L)
                .build();

        IVAN_SECOND_ORDER = Order.builder()
                .id(3000001L)
                .user(User.builder().id(1000000L).build())
                .createdAt(LocalDateTime.parse("2021-11-04T17:52:19"))
                .expectedRetrieveDate(LocalDateTime.parse("2021-11-05T15:52:19"))
                .status(OrderStatus.CANCELED)
                .accruedPoints(0L)
                .debitedPoints(0L)
                .totalPrice(400L)
                .build();

        HANS_ORDER = Order.builder()
                .id(3000002L)
                .user(User.builder().id(1000003L).build())
                .createdAt(LocalDateTime.parse("2021-11-05T15:35:36"))
                .expectedRetrieveDate(LocalDateTime.parse("2021-11-06T15:35:36"))
                .status(OrderStatus.PENDING)
                .accruedPoints(0L)
                .debitedPoints(250L)
                .totalPrice(4000L)
                .build();

        PETR_ORDER = Order.builder()
                .id(3000003L)
                .user(User.builder().id(1000001L).build())
                .createdAt(LocalDateTime.parse("2021-11-06T10:12:36"))
                .expectedRetrieveDate(LocalDateTime.parse("2021-11-06T15:12:36"))
                .status(OrderStatus.PENDING)
                .accruedPoints(0L)
                .debitedPoints(90L)
                .totalPrice(2500L)
                .build();
    }

    private final Order johnOrder;
    private final Order petrOrderClone;

    private final OrderDaoImpl orderDao;

    OrderDaoImplTest(OrderDaoImpl orderDao) {
        this.orderDao = orderDao;

        johnOrder = Order.builder()
                .user(User.builder().id(1000002L).build())
                .createdAt(LocalDateTime.parse("2021-11-07T10:00:00"))
                .expectedRetrieveDate(LocalDateTime.parse("2021-11-07T12:11:00"))
                .status(OrderStatus.PENDING)
                .accruedPoints(0L)
                .debitedPoints(350L)
                .totalPrice(250L)
                .build();

        petrOrderClone = Order.builder()
                .id(3000003L)
                .user(User.builder().id(1000001L).build())
                .createdAt(LocalDateTime.parse("2021-11-06T10:12:36"))
                .expectedRetrieveDate(LocalDateTime.parse("2021-11-06T15:12:36"))
                .status(OrderStatus.PENDING)
                .accruedPoints(0L)
                .debitedPoints(90L)
                .totalPrice(2500L)
                .build();
    }

    public static Stream<Arguments> dataForFindByStatusMethod() {
        return Stream.of(
                Arguments.of(OrderStatus.PENDING.toString(), List.of(HANS_ORDER, PETR_ORDER)),
                Arguments.of(OrderStatus.COMPLETED.toString(), List.of(IVAN_FIRST_ORDER)),
                Arguments.of(OrderStatus.CANCELED.toString(), List.of(IVAN_SECOND_ORDER)),
                Arguments.of(OrderStatus.NOT_COLLECTED.toString(), Collections.emptyList())
        );
    }

    public static Stream<Arguments> dataForFindByUserIdMethod() {
        return Stream.of(
                Arguments.of(1000003L, List.of(HANS_ORDER)),
                Arguments.of(1000000L, List.of(IVAN_FIRST_ORDER, IVAN_SECOND_ORDER)),
                Arguments.of(1000001L, List.of(PETR_ORDER)),
                Arguments.of(1000002L, Collections.emptyList())
        );
    }

    public static Stream<Arguments> dataForFindByCreatedAtBetweenMethod() {
        return Stream.of(
                Arguments.of(LocalDateTime.parse("2021-11-04T00:00:00"), LocalDateTime.parse("2021-11-05T00:00:00"), List.of(IVAN_FIRST_ORDER, IVAN_SECOND_ORDER)),
                Arguments.of(LocalDateTime.parse("2021-11-05T15:00:00"), LocalDateTime.parse("2021-11-05T16:00:00"), List.of(HANS_ORDER)),
                Arguments.of(LocalDateTime.parse("2021-11-02T00:00:00"), LocalDateTime.parse("2021-11-10T00:00:00"), List.of(IVAN_FIRST_ORDER, IVAN_SECOND_ORDER, HANS_ORDER, PETR_ORDER)),
                Arguments.of(LocalDateTime.parse("2021-11-10T00:00:00"), LocalDateTime.parse("2021-11-20T00:00:00"), Collections.emptyList())
        );
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(INSERT_INTO_USERS_SQL);
            statement.executeUpdate(INSERT_INTO_ORDERS_SQL);
        }
    }

    @Test
    @Tag("findAll")
    void shouldReturnAllOrdersFromDatabase() throws DaoException {
        List<Order> expected = List.of(IVAN_FIRST_ORDER, IVAN_SECOND_ORDER, HANS_ORDER, PETR_ORDER);

        List<Order> actual = orderDao.findAll();

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @Test
    @Tag("findById")
    void shouldReturnExistingOrderById() throws DaoException {
        Order expected = IVAN_SECOND_ORDER;

        Optional<Order> optionalComment = orderDao.findById(3000001L);

        optionalComment.ifPresentOrElse(
                (actual) -> assertEquals(expected, actual, () -> "expected to get:" + expected),
                Assertions::fail
        );
    }

    @Test
    @Tag("findById")
    void shouldReturnEmptyOptionalIfThereIsNoSuchOrderId() throws DaoException {
        Optional<Order> actual = orderDao.findById(-1L);

        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("create")
    void shouldCreatedCommentInDatabase() throws DaoException, SQLException {
        Order expected = johnOrder;

        orderDao.create(expected);
        Order actual = findById(expected.getId());

        assertEquals(expected, actual, () -> "must create: " + expected);
    }

    @Test
    @Tag("update")
    void shouldUpdateCommentInDatabase() throws DaoException, SQLException {
        Order expected = petrOrderClone;
        expected.setStatus(OrderStatus.COMPLETED);
        expected.setActualRetrieveDate(LocalDateTime.parse("2021-11-06T15:12:36"));
        expected.setAccruedPoints(20L);

        boolean isUpdated = orderDao.update(expected);
        Order actual = findById(expected.getId());
        assertAll(
                () -> assertTrue(isUpdated, "must return true if order was updated in the database"),
                () -> assertEquals(expected, actual, () -> "must update in database to: " + expected)
        );
    }

    @Test
    @Tag("update")
    void shouldReturnFalseIfCommentWasNotUpdated() throws DaoException {
        petrOrderClone.setId(-1L);
        boolean isUpdated = orderDao.update(petrOrderClone);

        assertFalse(isUpdated, "mustn't update any orders if no such order id");
    }

    @Test
    @Tag("delete")
    void shouldDeleteCommentFromDatabaseById() throws DaoException, SQLException {
        Order expected = petrOrderClone;

        boolean isDeleted = orderDao.delete(expected.getId());
        Order actual = findById(expected.getId());

        assertAll(
                () -> assertTrue(isDeleted, "must return true if order was deleted from the database"),
                () -> assertNull(actual, () -> "must delete %s from database".formatted(expected))
        );
    }

    @Test
    @Tag("delete")
    void shouldReturnFalseIfCommentWasNotDeleted() throws DaoException {
        boolean isDeleted = orderDao.delete(-1L);

        assertFalse(isDeleted, "mustn't delete any orders if no such order id");
    }

    @ParameterizedTest
    @MethodSource("dataForFindByStatusMethod")
    @Tag("findByStatus")
    void shouldReturnAllOrdersWithGivenStatus(String status, List<Order> expected) throws DaoException {
        List<Order> actual = orderDao.findByStatus(status);

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @ParameterizedTest
    @MethodSource("dataForFindByUserIdMethod")
    @Tag("findByUserId")
    void shouldReturnAllUserOrders(Long userId, List<Order> expected) throws DaoException {
        List<Order> actual = orderDao.findByUserId(userId);

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @ParameterizedTest
    @MethodSource("dataForFindByCreatedAtBetweenMethod")
    @Tag("findByCreatedAtBetween")
    void shouldReturnAllOrdersWithCreatedDateBetween(LocalDateTime from, LocalDateTime to, List<Order> expected) throws DaoException {
        List<Order> actual = orderDao.findByCreatedAtBetween(from, to);

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(DELETE_ORDERS_SQL);
            statement.executeUpdate(DELETE_USERS_SQL);
        }
    }

    @AfterAll
    static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Order findById(Long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Timestamp timestamp = resultSet.getObject(ACTUAL_RETRIEVE_DATE_COLUMN_NAME, Timestamp.class);
                LocalDateTime actualRetrieveDate = timestamp != null
                        ? timestamp.toLocalDateTime()
                        : null;
                return Order.builder()
                        .id(resultSet.getObject(ID_COLUMN_NAME, Long.class))
                        .user(User.builder().id(resultSet.getObject(USER_ID_COLUMN_NAME, Long.class)).build())
                        .createdAt(resultSet.getObject(CREATED_AT_COLUMN_NAME, Timestamp.class).toLocalDateTime())
                        .expectedRetrieveDate(resultSet.getObject(EXPECTED_RETRIEVE_DATE_COLUMN_NAME, Timestamp.class).toLocalDateTime())
                        .actualRetrieveDate(actualRetrieveDate)
                        .status(OrderStatus.valueOf(resultSet.getObject(STATUS_COLUMN_NAME, String.class)))
                        .debitedPoints(resultSet.getObject(DEBITED_POINTS_COLUMN_NAME, Long.class))
                        .accruedPoints(resultSet.getObject(ACCRUED_POINTS_COLUMN_NAME, Long.class))
                        .totalPrice(resultSet.getObject(TOTAL_PRICE_COLUMN_NAME, Long.class))
                        .build();
            }
        }
        return null;
    }
}
