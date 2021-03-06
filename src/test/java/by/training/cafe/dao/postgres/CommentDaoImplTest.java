package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.pool.ConnectionPool;
import by.training.cafe.entity.Comment;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.User;
import by.training.cafe.extension.DatabaseExtension;
import by.training.cafe.extension.SqlDaoParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({
        DatabaseExtension.class,
        SqlDaoParamResolver.class
})
class CommentDaoImplTest {

    private static final String ID_COLUMN_NAME = "id";
    private static final String USER_ID_COLUMN_NAME = "user_id";
    private static final String DISH_ID_COLUMN_NAME = "dish_id";
    private static final String RATING_COLUMN_NAME = "rating";
    private static final String BODY_COLUMN_NAME = "body";
    private static final String CREATED_AT_COLUMN_NAME = "created_at";

    private static final String INSERT_INTO_USERS_SQL = """
            INSERT INTO users (id, email, password, role, first_name, last_name, phone, points, is_blocked)
            VALUES (1000000, 'ivan@gmail.com', '$2a$10$.IVAN.PASS.43WRAZ3Fnyx.C/6PveEHf6JGzGo9X2SQSwM5djXdrO', 'CLIENT', 'Ivan', 'Melnikov','+375251111111',30,TRUE),
                   (1000001, 'petr@mail.ru', '$2a$10$.ПЕТР.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr1', 'CLIENT', 'Петр', 'Шариков','+375442222222',130,FALSE),
                   (1000002, 'john@gmail.com', '$2a$10$.John.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr2', 'ADMIN', 'John', 'Henson','+375333333333',270,FALSE),
                   (1000003, 'hans@gmail.com', '$2a$10$.Hans.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr3', 'CLIENT', 'Hans', 'Münz','+375254444444',0,FALSE)""";
    private static final String INSERT_INTO_DISH_SQL = """
            INSERT INTO dish (id, name, category, price, description)
            VALUES (1000000, 'Four seasons', 'PIZZA', 2000, 'Really delicious pizza!'),
                   (1000001, 'Chicken BBQ', 'PIZZA', 2500, 'Pizza with chicken and sauce BBQ'),
                   (1000002, 'Coca-Cola 1L', 'DRINKS', 200, 'Soft drink with caffeine and plant extracts.')""";
    private static final String INSERT_INTO_COMMENT_SQL = """
            INSERT INTO comment (id, user_id, dish_id, rating, body, created_at)
            VALUES (5000001, 1000001, 1000000, 3, 'Normal', '2021-11-03 10:25:06'),
                   (5000002, 1000002, 1000000, 5, 'Good :)', '2021-11-04 15:35:36'),
                   (5000003, 1000001, 1000001, 5, 'Perfect!', '2021-11-05 17:28:12')""";
    private static final String DELETE_DISHES_SQL = "DELETE FROM dish";
    private static final String DELETE_USERS_SQL = "DELETE FROM users";
    private static final String DELETE_COMMENTS_SQL = "DELETE FROM comment";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM comment WHERE id = ?";
    private static final Connection CONNECTION = ConnectionPool.getInstance().getConnection();

    private static final Comment PETR_ABOUT_FOUR_SEASONS;
    private static final Comment PETR_ABOUT_CHICKEN_BBQ;
    private static final Comment JOHN_ABOUT_FOUR_SEASONS;

    static {
        PETR_ABOUT_FOUR_SEASONS = Comment.builder()
                .id(5000001L)
                .user(User.builder().id(1000001L).build())
                .dish(Dish.builder().id(1000000L).build())
                .rating((short) 3)
                .body("Normal")
                .createdAt(Timestamp.valueOf("2021-11-03 10:25:06"))
                .build();

        PETR_ABOUT_CHICKEN_BBQ = Comment.builder()
                .id(5000003L)
                .user(User.builder().id(1000001L).build())
                .dish(Dish.builder().id(1000001L).build())
                .rating((short) 5)
                .body("Perfect!")
                .createdAt(Timestamp.valueOf("2021-11-05 17:28:12"))
                .build();

        JOHN_ABOUT_FOUR_SEASONS = Comment.builder()
                .id(5000002L)
                .user(User.builder().id(1000002L).build())
                .dish(Dish.builder().id(1000000L).build())
                .rating((short) 5)
                .body("Good :)")
                .createdAt(Timestamp.valueOf("2021-11-04 15:35:36"))
                .build();
    }

    private final Comment johnAboutFourSeasonsClone;
    private final Comment hansAboutCola;

    private final CommentDaoImpl commentDao;

    CommentDaoImplTest(CommentDaoImpl commentDao) {
        this.commentDao = commentDao;

        johnAboutFourSeasonsClone = Comment.builder()
                .id(5000002L)
                .user(User.builder().id(1000002L).build())
                .dish(Dish.builder().id(1000000L).build())
                .rating((short) 5)
                .body("Good :)")
                .createdAt(Timestamp.valueOf("2021-11-04 15:35:36"))
                .build();

        hansAboutCola = Comment.builder()
                .user(User.builder().id(1000003L).build())
                .dish(Dish.builder().id(1000002L).build())
                .rating((short) 2)
                .body("Nice")
                .createdAt(Timestamp.valueOf("2021-11-03 10:10:09"))
                .build();
    }

    public static Stream<Arguments> dataForFindByUserId() {
        return Stream.of(
                Arguments.of(1000001L, List.of(PETR_ABOUT_CHICKEN_BBQ, PETR_ABOUT_FOUR_SEASONS)),
                Arguments.of(1000002L, List.of(JOHN_ABOUT_FOUR_SEASONS)),
                Arguments.of(1000000L, Collections.emptyList()),
                Arguments.of(1000003L, Collections.emptyList())
        );
    }

    public static Stream<Arguments> dataForFindByDishId() {
        return Stream.of(
                Arguments.of(1000000L, List.of(JOHN_ABOUT_FOUR_SEASONS, PETR_ABOUT_FOUR_SEASONS)),
                Arguments.of(1000001L, List.of(PETR_ABOUT_CHICKEN_BBQ)),
                Arguments.of(1000002L, Collections.emptyList())
        );
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Statement statement = CONNECTION.createStatement()) {
            statement.executeUpdate(INSERT_INTO_USERS_SQL);
            statement.executeUpdate(INSERT_INTO_DISH_SQL);
            statement.executeUpdate(INSERT_INTO_COMMENT_SQL);
        }
    }

    @Test
    @Tag("findAll")
    void shouldReturnAllCommentsFromDatabase() throws DaoException {
        List<Comment> expected = List.of(PETR_ABOUT_FOUR_SEASONS, johnAboutFourSeasonsClone, PETR_ABOUT_CHICKEN_BBQ);

        List<Comment> actual = commentDao.findAll();

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @Test
    @Tag("findById")
    void shouldReturnExistingCommentById() throws DaoException {
        Comment expected = PETR_ABOUT_CHICKEN_BBQ;

        Optional<Comment> optionalComment = commentDao.findById(5000003L);

        optionalComment.ifPresentOrElse(
                (actual) -> assertEquals(expected, actual, () -> "expected to get:" + expected),
                Assertions::fail
        );
    }

    @Test
    @Tag("findById")
    void shouldReturnEmptyOptionalIfThereIsNoSuchCommentId() throws DaoException {
        Optional<Comment> actual = commentDao.findById(-1L);

        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("create")
    void shouldCreateCommentInDatabase() throws DaoException, SQLException {
        Comment expected = hansAboutCola;

        commentDao.create(expected);
        Comment actual = findById(expected.getId());

        assertEquals(expected, actual, () -> "must create: " + expected);
    }

    @Test
    @Tag("update")
    void shouldUpdateCommentInDatabase() throws DaoException, SQLException {
        Comment expected = johnAboutFourSeasonsClone;
        expected.setBody("It's the best!");

        boolean isUpdated = commentDao.update(expected);
        Comment actual = findById(expected.getId());
        assertAll(
                () -> assertTrue(isUpdated, "must return true if comment was updated in the database"),
                () -> assertEquals(expected, actual, () -> "must update in database to: " + expected)
        );
    }

    @Test
    @Tag("update")
    void shouldReturnFalseIfCommentWasNotUpdated() throws DaoException {
        johnAboutFourSeasonsClone.setId(-1L);
        boolean isUpdated = commentDao.update(johnAboutFourSeasonsClone);

        assertFalse(isUpdated, "mustn't update any comments if no such comment id");
    }

    @Test
    @Tag("delete")
    void shouldDeleteCommentFromDatabaseById() throws DaoException, SQLException {
        Comment expected = PETR_ABOUT_FOUR_SEASONS;

        boolean isDeleted = commentDao.delete(expected.getId());
        Comment actual = findById(expected.getId());

        assertAll(
                () -> assertTrue(isDeleted, "must return true if comment was deleted from the database"),
                () -> assertNull(actual, () -> "must delete %s from database".formatted(expected))
        );
    }

    @Test
    @Tag("delete")
    void shouldReturnFalseIfCommentWasNotDeleted() throws DaoException {
        boolean isDeleted = commentDao.delete(-1L);

        assertFalse(isDeleted, "mustn't delete any comments if no such comment id");
    }

    @ParameterizedTest
    @MethodSource("dataForFindByUserId")
    @Tag("findByUserId")
    void shouldReturnListOfCommentsByUserId(Long userId, List<Comment> expected) throws DaoException {
        List<Comment> actual = commentDao.findByUserIdOrderByCreatedAtDesc(userId);

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @ParameterizedTest
    @MethodSource("dataForFindByDishId")
    @Tag("findByDishId")
    void findByDishId(Long dishId, List<Comment> expected) throws DaoException {
        List<Comment> actual = commentDao.findByDishIdOrderByCreatedAtDesc(dishId);

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = CONNECTION.createStatement()) {
            statement.executeUpdate(DELETE_COMMENTS_SQL);
            statement.executeUpdate(DELETE_USERS_SQL);
            statement.executeUpdate(DELETE_DISHES_SQL);
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

    private Comment findById(Long id) throws SQLException {
        try (PreparedStatement statement = CONNECTION.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Comment.builder()
                        .id(resultSet.getObject(ID_COLUMN_NAME, Long.class))
                        .user(User.builder().id(resultSet.getObject(USER_ID_COLUMN_NAME, Long.class)).build())
                        .dish(Dish.builder().id(resultSet.getObject(DISH_ID_COLUMN_NAME, Long.class)).build())
                        .rating(resultSet.getObject(RATING_COLUMN_NAME, Short.class))
                        .body(resultSet.getObject(BODY_COLUMN_NAME, String.class))
                        .createdAt(resultSet.getObject(CREATED_AT_COLUMN_NAME, Timestamp.class))
                        .build();
            }
        }
        return null;
    }
}
