package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.UserDao;
import by.training.cafe.entity.Language;
import by.training.cafe.entity.User;
import by.training.cafe.entity.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The class {@code CommentDaoImpl} is a class that extends
 * {@link AbstractSqlDao} and implements {@link UserDao}.<br/>
 * Provides access to the PostgreSQL database.
 *
 * @author Nikita Romanov
 * @see AbstractSqlDao
 * @see UserDao
 */
public class UserDaoImpl extends AbstractSqlDao<Long, User> implements UserDao {

    private static final Logger log = LogManager.getLogger(UserDaoImpl.class);

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

    private static final String FIND_ALL_SQL = """
            SELECT id, email, password, role, first_name,
            last_name, phone, points, is_blocked, language
            FROM users""";
    private static final String FIND_BY_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "id = ?";
    private static final String FIND_BY_EMAIL
            = FIND_ALL_SQL + WHERE_SQL + "email = ?";
    private static final String FIND_BY_PHONE
            = FIND_ALL_SQL + WHERE_SQL + "phone = ?";
    private static final String FIND_BY_ROLE
            = FIND_ALL_SQL + WHERE_SQL + "role = ?::user_role";
    private static final String CREATE_SQL = """
            INSERT INTO users (email, role, first_name,
            last_name, phone, points, is_blocked, language, password)
            VALUES (?, ?::user_role, ?, ?, ?, ?, ?, ?::app_language, ?)""";
    private static final String UPDATE_SQL = """
            UPDATE users
            SET email      = ?,
                role       = ?::user_role,
                first_name = ?,
                last_name  = ?,
                phone      = ?,
                points     = ?,
                is_blocked = ?,
                language   = ?::app_language
            WHERE id = ?""";
    private static final String UPDATE_PASSWORD_SQL = """
            UPDATE users
            SET password = ?
            WHERE id = ?""";
    private static final String DELETE_SQL = """
            DELETE FROM users
            WHERE id = ?""";

    public UserDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List<User> findAll() throws DaoException {
        List<User> users = executeSelectQuery(
                FIND_ALL_SQL, Collections.emptyList());
        log.debug(RESULT_LOG_MESSAGE, users);
        return users;
    }

    @Override
    public Optional<User> findById(Long id) throws DaoException {
        log.debug("Received id: {}", id);
        List<User> list = executeSelectQuery(
                FIND_BY_ID_SQL, List.of(id));
        Optional<User> maybeUser = getFirstEntityFromList(list);
        log.debug(RESULT_LOG_MESSAGE, maybeUser);
        return maybeUser;
    }

    @Override
    public void create(User entity) throws DaoException {
        log.debug("Received user: {}", entity);
        List<Object> params = createParamsList(entity);
        params.add(entity.getPassword());
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
    public boolean update(User entity) throws DaoException {
        log.debug("Received user: {}", entity);
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
            log.debug("User with id {} was deleted", id);
        } else {
            log.warn("User with id {} wasn't deleted", id);
        }
        return isDeleted;
    }

    @Override
    public Optional<User> findByPhone(String phone) throws DaoException {
        log.debug("Received phone: {}", phone);
        List<User> list = executeSelectQuery(FIND_BY_PHONE, List.of(phone));
        Optional<User> maybeUser = getFirstEntityFromList(list);
        log.debug(RESULT_LOG_MESSAGE, maybeUser);
        return maybeUser;
    }

    @Override
    public Optional<User> findByEmail(String email) throws DaoException {
        log.debug("Received email: {}", email);
        List<User> list = executeSelectQuery(FIND_BY_EMAIL, List.of(email));
        Optional<User> maybeUser = getFirstEntityFromList(list);
        log.debug(RESULT_LOG_MESSAGE, maybeUser);
        return maybeUser;
    }

    @Override
    public List<User> findByRole(String role) throws DaoException {
        log.debug("Received role: {}", role);
        List<User> users = executeSelectQuery(FIND_BY_ROLE, List.of(role));
        log.debug(RESULT_LOG_MESSAGE, users);
        return users;
    }

    @Override
    public boolean updatePassword(Long id, String password) throws DaoException {
        log.debug("Received id = {}, password = {}", id, password);
        int updatedRows = executeUpdateQuery(
                UPDATE_PASSWORD_SQL, List.of(password, id));
        boolean isUpdated = isOnlyOneRowUpdated(updatedRows);
        if (isUpdated) {
            log.debug("Entity password with id = {} was updated", id);
        } else {
            log.warn("Entity password with id = {} wasn't updated", id);
        }
        return isUpdated;
    }

    @Override
    protected User buildEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(
                ID_COLUMN_NAME, Long.class);
        log.trace("id = {}", id);

        String email = resultSet.getObject(
                EMAIL_COLUMN_NAME, String.class);
        log.trace("email = {}", email);

        String password = resultSet.getObject(
                PASSWORD_COLUMN_NAME, String.class);
        log.trace("password = {}", password);

        UserRole role = UserRole.valueOf(resultSet.getObject(
                ROLE_COLUMN_NAME, String.class));
        log.trace("role = {}", role);

        String firstName = resultSet.getObject(
                FIRST_NAME_COLUMN_NAME, String.class);
        log.trace("firstName = {}", firstName);

        String lastName = resultSet.getObject(
                LAST_NAME_COLUMN_NAME, String.class);
        log.trace("lastName = {}", lastName);

        String phone = resultSet.getObject(
                PHONE_COLUMN_NAME, String.class);
        log.trace("phone = {}", phone);

        Long points = resultSet.getObject(
                POINTS_COLUMN_NAME, Long.class);
        log.trace("points = {}", points);

        Boolean isBlocked = resultSet.getObject(
                IS_BLOCKED_COLUMN_NAME, Boolean.class);
        log.trace("isBlocked = {}", isBlocked);

        Language language = Language.valueOf(resultSet.getObject(
                LANGUAGE_COLUMN_NAME, String.class));
        log.trace("language = {}", language);

        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .role(role)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .points(points)
                .isBlocked(isBlocked)
                .language(language)
                .build();
    }

    private List<Object> createParamsList(User user) {
        List<Object> params = new ArrayList<>();
        params.add(user.getEmail());
        params.add(user.getRole().toString());
        params.add(user.getFirstName());
        params.add(user.getLastName());
        params.add(user.getPhone());
        params.add(user.getPoints());
        params.add(user.isBlocked());
        params.add(user.getLanguage().toString());
        return params;
    }
}
