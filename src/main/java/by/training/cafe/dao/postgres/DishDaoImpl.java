package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.DishDao;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.DishCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The class {@code DishDaoImpl} is a class that extends
 * {@link AbstractSqlDao} and implements {@link DishDao}.<br/>
 * Provides access to the PostgreSQL database.
 *
 * @author Nikita Romanov
 * @see AbstractSqlDao
 * @see DishDao
 */
public class DishDaoImpl extends AbstractSqlDao<Long, Dish> implements DishDao {

    private static final Logger log = LogManager.getLogger(DishDaoImpl.class);

    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String CATEGORY_COLUMN_NAME = "category";
    private static final String PRICE_COLUMN_NAME = "price";
    private static final String DESCRIPTION_COLUMN_NAME = "description";

    private static final String FIND_ALL_SQL = """
            SELECT id, name, category, price, description
            FROM dish""";
    private static final String FIND_ALL_WITH_LIMIT_AND_OFFSET_SQL
            = FIND_ALL_SQL + LIMIT_SQL + OFFSET_SQL;
    private static final String FIND_BY_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "id = ?";
    private static final String NAME_LIKE_OR_DESCRIPTION_LIKE_SQL
            = "(name ILIKE ?" + OR_SQL + "description ILIKE ?)";
    private static final String FIND_BY_NAME_LIKE_OR_DESCRIPTION_LIKE_SQL
            = FIND_ALL_SQL + WHERE_SQL + NAME_LIKE_OR_DESCRIPTION_LIKE_SQL;
    private static final String FIND_BY_CATEGORY_SQL
            = FIND_ALL_SQL + WHERE_SQL + "category = ?::dish_category";
    private static final String CREATE_SQL = """
            INSERT INTO dish (name, category, price, description)
            VALUES (?, ?::dish_category, ?, ?)""";
    private static final String UPDATE_SQL = """
            UPDATE dish
            SET name        = ?,
                category    = ?::dish_category,
                price       = ?,
                description = ?
            WHERE id = ?""";
    private static final String DELETE_SQL = """
            DELETE FROM dish
            WHERE id = ?""";
    private static final String COUNT_SQL = """
            SELECT count(id)
            FROM dish""";

    public DishDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List<Dish> findAll() throws DaoException {
        List<Dish> dishes = executeSelectQuery(
                FIND_ALL_SQL, Collections.emptyList());
        log.debug(RESULT_LOG_MESSAGE, dishes);
        return dishes;
    }

    @Override
    public List<Dish> findAll(Long limit, Long offset) throws DaoException {
        log.debug("Received limit = {}, offset = {}", limit, offset);
        List<Dish> dishes = executeSelectQuery(
                FIND_ALL_WITH_LIMIT_AND_OFFSET_SQL, List.of(limit, offset));
        log.debug(RESULT_LOG_MESSAGE, dishes);
        return dishes;
    }

    @Override
    public Long count() throws DaoException {
        Long count = executeCountQuery(COUNT_SQL);
        log.debug("Count result: {}", count);
        return count;
    }

    @Override
    public Optional<Dish> findById(Long id) throws DaoException {
        log.debug("Received id: {}", id);
        List<Dish> list = executeSelectQuery(
                FIND_BY_ID_SQL, List.of(id));
        Optional<Dish> maybeDish = getFirstEntityFromList(list);
        log.debug(RESULT_LOG_MESSAGE, maybeDish);
        return maybeDish;
    }

    @Override
    public void create(Dish entity) throws DaoException {
        log.debug("Received dish: {}", entity);
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
    public boolean update(Dish entity) throws DaoException {
        log.debug("Received dish: {}", entity);
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
            log.debug("Dish with id {} was deleted", id);
        } else {
            log.warn("Dish with id {} wasn't deleted", id);
        }
        return isDeleted;
    }

    @Override
    public List<Dish> findByNameOrDescriptionLike(List<String> words)
            throws DaoException {
        log.debug("Received words: {}", words);
        StringBuilder sqlBuilder = new StringBuilder(FIND_BY_NAME_LIKE_OR_DESCRIPTION_LIKE_SQL);
        for (int i = 1; i < words.size(); i++) {
            sqlBuilder.append(AND_SQL)
                    .append(NAME_LIKE_OR_DESCRIPTION_LIKE_SQL);
        }
        List<String> sqlWords = words.stream()
                .map(word -> PERCENT + word + PERCENT)
                .toList();
        log.debug("Modified words: {}", sqlWords);
        try (PreparedStatement prepareStatement
                     = connection.prepareStatement(sqlBuilder.toString())) {
            int counter = 0;
            for (String sqlWord : sqlWords) {
                prepareStatement.setObject(++counter, sqlWord);
                prepareStatement.setObject(++counter, sqlWord);
            }
            log.debug(EXECUTING_SQL_LOG_MESSAGE, prepareStatement);
            ResultSet resultSet = prepareStatement.executeQuery();
            List<Dish> dishes = new ArrayList<>();
            while (resultSet.next()) {
                dishes.add(buildEntity(resultSet));
            }
            log.debug(RESULT_LOG_MESSAGE, dishes);
            return dishes;
        } catch (SQLException e) {
            throw new DaoException(SQL_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    @Override
    public List<Dish> findByCategory(String category) throws DaoException {
        log.debug("Received category: {}", category);
        List<Dish> dishes = executeSelectQuery(
                FIND_BY_CATEGORY_SQL, List.of(category));
        log.debug(RESULT_LOG_MESSAGE, dishes);
        return dishes;
    }

    @Override
    protected Dish buildEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(
                ID_COLUMN_NAME, Long.class);
        log.trace("id = {}", id);

        String name = resultSet.getObject(
                NAME_COLUMN_NAME, String.class);
        log.trace("name = {}", name);

        DishCategory category = DishCategory.valueOf(resultSet.getObject(
                CATEGORY_COLUMN_NAME, String.class));
        log.trace("category = {}", category);

        Long price = resultSet.getObject(
                PRICE_COLUMN_NAME, Long.class);
        log.trace("price = {}", price);

        String description = resultSet.getObject(
                DESCRIPTION_COLUMN_NAME, String.class);
        log.trace("description = {}", description);

        return Dish.builder()
                .id(id)
                .name(name)
                .category(category)
                .price(price)
                .description(description)
                .build();
    }

    private List<Object> createParamsList(Dish dish) {
        List<Object> params = new ArrayList<>();
        params.add(dish.getName());
        params.add(dish.getCategory().toString());
        params.add(dish.getPrice());
        params.add(dish.getDescription());
        return params;
    }
}
