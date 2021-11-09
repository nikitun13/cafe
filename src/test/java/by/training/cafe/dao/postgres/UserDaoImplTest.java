package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.pool.ConnectionPool;
import by.training.cafe.entity.User;
import by.training.cafe.entity.UserRole;
import by.training.cafe.extension.DatabaseExtension;
import by.training.cafe.extension.SqlDaoParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({
        DatabaseExtension.class,
        SqlDaoParamResolver.class
})
class UserDaoImplTest {

    private static final String ID_COLUMN_NAME = "id";
    private static final String EMAIL_COLUMN_NAME = "email";
    private static final String PASSWORD_COLUMN_NAME = "password";
    private static final String ROLE_COLUMN_NAME = "role";
    private static final String FIRST_NAME_COLUMN_NAME = "first_name";
    private static final String LAST_NAME_COLUMN_NAME = "last_name";
    private static final String PHONE_COLUMN_NAME = "phone";
    private static final String POINTS_COLUMN_NAME = "points";
    private static final String IS_BLOCKED_COLUMN_NAME = "is_blocked";
    private static final String LANGUAGE_COLUMN_NAME = "language";

    private static final String SET_UP_SQL = """
            INSERT INTO users (id, email, password, role, first_name, last_name, phone, points, is_blocked)
            VALUES (1000000, 'ivan@gmail.com', '$2a$10$.IVAN.PASS.43WRAZ3Fnyx.C/6PveEHf6JGzGo9X2SQSwM5djXdrO', 'CLIENT', 'Ivan', 'Melnikov','+375251111111',30,TRUE),
                   (1000001, 'petr@mail.ru', '$2a$10$.ПЕТР.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr1', 'CLIENT', 'Петр', 'Шариков','+375442222222',130,FALSE),
                   (1000002, 'john@gmail.com', '$2a$10$.John.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr2', 'ADMIN', 'John', 'Henson','+375333333333',270,FALSE),
                   (1000003, 'hans@gmail.com', '$2a$10$.Hans.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr3', 'CLIENT', 'Hans', 'Münz','+375254444444',0,FALSE)""";
    private static final String TEAR_DOWN_SQL = "DELETE FROM users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";

    private static final Connection CONNECTION = ConnectionPool.getInstance().getConnection();
    private static final User IVAN;
    private static final User PETR;
    private static final User JOHN;

    static {
        IVAN = User.builder()
                .id(1_000_000L)
                .email("ivan@gmail.com")
                .password("$2a$10$.IVAN.PASS.43WRAZ3Fnyx.C/6PveEHf6JGzGo9X2SQSwM5djXdrO")
                .role(UserRole.CLIENT)
                .firstName("Ivan")
                .lastName("Melnikov")
                .phone("+375251111111")
                .points(30L)
                .isBlocked(true)
                .build();

        PETR = User.builder()
                .id(1_000_001L)
                .email("petr@mail.ru")
                .password("$2a$10$.ПЕТР.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr1")
                .role(UserRole.CLIENT)
                .firstName("Петр")
                .lastName("Шариков")
                .phone("+375442222222")
                .points(130L)
                .isBlocked(false)
                .build();

        JOHN = User.builder()
                .id(1_000_002L)
                .email("john@gmail.com")
                .password("$2a$10$.John.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr2")
                .role(UserRole.ADMIN)
                .firstName("John")
                .lastName("Henson")
                .phone("+375333333333")
                .points(270L)
                .isBlocked(false)
                .build();
    }

    private final User hans;
    private final User adam;

    private final UserDaoImpl userDao;

    UserDaoImplTest(UserDaoImpl userDao) {
        this.userDao = userDao;

        hans = User.builder()
                .id(1_000_003L)
                .email("hans@gmail.com")
                .password("$2a$10$.Hans.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr3")
                .role(UserRole.CLIENT)
                .firstName("Hans")
                .lastName("Münz")
                .phone("+375254444444")
                .points(0L)
                .isBlocked(false)
                .build();

        adam = User.builder()
                .email("smith@gmail.com")
                .password("$2a$10$.Adam.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr4")
                .role(UserRole.CLIENT)
                .firstName("Adam")
                .lastName("Smith")
                .phone("+375445555555")
                .points(500L)
                .isBlocked(false)
                .build();
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (PreparedStatement statement = CONNECTION.prepareStatement(SET_UP_SQL)) {
            statement.executeUpdate();
        }
    }

    @Test
    @Tag("findAll")
    void shouldReturnAllUsersFromDatabase() throws DaoException {
        List<User> expected = List.of(IVAN, PETR, JOHN, hans);

        List<User> actual = userDao.findAll();

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @Test
    @Tag("findById")
    void shouldReturnExistingUserById() throws DaoException {
        User expected = JOHN;

        Optional<User> optionalUser = userDao.findById(1_000_002L);

        optionalUser.ifPresentOrElse(
                (actual) -> assertEquals(expected, actual, () -> "expected to get:" + expected),
                Assertions::fail
        );
    }

    @Test
    @Tag("findById")
    void shouldReturnEmptyOptionalIfThereIsNoSuchUserId() throws DaoException {
        Optional<User> actual = userDao.findById(-1L);

        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("create")
    void shouldCreateUserInDatabase() throws DaoException, SQLException {
        User expected = adam;

        userDao.create(expected);
        User actual = findById(expected.getId());

        assertEquals(expected, actual, () -> "must create: " + expected);
    }

    @Test
    @Tag("update")
    void shouldUpdateUserInDatabase() throws DaoException, SQLException {
        User expected = hans;
        expected.setPoints(319L);
        expected.setPhone("+49302291110");

        boolean isUpdated = userDao.update(expected);
        User actual = findById(expected.getId());
        assertAll(
                () -> assertTrue(isUpdated, "must return true if user was updated in the database"),
                () -> assertEquals(expected, actual, () -> "must update in database to: " + expected)
        );
    }

    @Test
    @Tag("update")
    void shouldReturnFalseIfUserWasNotUpdated() throws DaoException {
        adam.setId(-1L);
        boolean isUpdated = userDao.update(adam);

        assertFalse(isUpdated, "mustn't update any users if no such user id");
    }

    @Test
    @Tag("delete")
    void shouldDeleteUserFromDatabaseById() throws DaoException, SQLException {
        User expected = hans;

        boolean isDeleted = userDao.delete(expected.getId());
        User actual = findById(expected.getId());

        assertAll(
                () -> assertTrue(isDeleted, "must return true if user was deleted from database"),
                () -> assertNull(actual, () -> "must delete %s from database".formatted(expected))
        );
    }

    @Test
    @Tag("delete")
    void shouldReturnFalseIfUserWasNotDeleted() throws DaoException {
        boolean isDeleted = userDao.delete(-1L);

        assertFalse(isDeleted, "mustn't delete any users if no such user id");
    }

    @Test
    @Tag("findByPhone")
    void shouldReturnUserByPhone() throws DaoException {
        User expected = IVAN;

        Optional<User> optionalUser = userDao.findByPhone("+375251111111");

        optionalUser.ifPresentOrElse(
                (actual) -> assertEquals(expected, actual, () -> "expected to get:" + expected),
                Assertions::fail
        );
    }

    @Test
    @Tag("findByPhone")
    void shouldReturnEmptyOptionalIfThereIsNoSuchPhone() throws DaoException {
        Optional<User> maybeActual = userDao.findByPhone("+375000000000");

        assertTrue(maybeActual.isEmpty());
    }

    @Test
    @Tag("findByEmail")
    void shouldFindUserById() throws DaoException {
        User expected = PETR;

        Optional<User> optionalUser = userDao.findByEmail("petr@mail.ru");

        optionalUser.ifPresentOrElse(
                (actual) -> assertEquals(expected, actual, () -> "expected to get:" + expected),
                Assertions::fail
        );
    }

    @Test
    @Tag("findByEmail")
    void shouldReturnEmptyOptionalIfThereIsNoSuchEmail() throws DaoException {
        Optional<User> maybeActual = userDao.findByEmail("no@such.email");

        assertTrue(maybeActual.isEmpty());
    }

    @Test
    @Tag("findByRole")
    void shouldReturnAllClients() throws DaoException {
        List<User> expected = List.of(IVAN, PETR, hans);

        List<User> actual = userDao.findByRole("CLIENT");

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @Test
    @Tag("findByRole")
    void shouldReturnAllAdmins() throws DaoException {
        List<User> expected = List.of(JOHN);

        List<User> actual = userDao.findByRole("ADMIN");

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @Test
    @Tag("findByRole")
    void shouldThrowExceptionIfThereIsNoSuchRole() {
        assertThrows(DaoException.class,
                () -> userDao.findByRole("noSuchRole"),
                () -> "Must throw %s if there is no such role".formatted(DaoException.class.getName()));
    }

    @Test
    @Tag("updatePassword")
    void shouldUpdatePasswordInDatabase() throws DaoException, SQLException {
        Long id = hans.getId();
        String expected = "$2a$10$.NEW.HANS.PASS.AZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr1";

        boolean isUpdated = userDao.updatePassword(id, expected);
        String actual = findById(id).getPassword();

        assertAll(
                () -> assertTrue(isUpdated, "must return true if password was updated in the database"),
                () -> assertEquals(expected, actual, () -> "should update password in db to: " + expected)
        );
    }

    @Test
    @Tag("updatePassword")
    void shouldReturnFalseIfPasswordWasNotUpdated() throws DaoException {
        String dummy = "$2a$10$.NEW.HANS.PASS.AZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr1";
        boolean isUpdated = userDao.updatePassword(-1L, dummy);

        assertFalse(isUpdated, "mustn't update any users if no such user id");
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (PreparedStatement statement = CONNECTION.prepareStatement(TEAR_DOWN_SQL)) {
            statement.executeUpdate();
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

    private User findById(Long id) throws SQLException {
        try (PreparedStatement statement = CONNECTION.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return User.builder()
                        .id(resultSet.getObject(ID_COLUMN_NAME, Long.class))
                        .email(resultSet.getObject(EMAIL_COLUMN_NAME, String.class))
                        .password(resultSet.getObject(PASSWORD_COLUMN_NAME, String.class))
                        .role(UserRole.valueOf(resultSet.getObject(ROLE_COLUMN_NAME, String.class)))
                        .firstName(resultSet.getObject(FIRST_NAME_COLUMN_NAME, String.class))
                        .lastName(resultSet.getObject(LAST_NAME_COLUMN_NAME, String.class))
                        .phone(resultSet.getObject(PHONE_COLUMN_NAME, String.class))
                        .points(resultSet.getObject(POINTS_COLUMN_NAME, Long.class))
                        .isBlocked(resultSet.getObject(IS_BLOCKED_COLUMN_NAME, Boolean.class))
                        .build();
            }
        }
        return null;
    }
}
