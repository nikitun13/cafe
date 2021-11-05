package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.DishDao;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.DishCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DishDaoImpl extends AbstractSqlDao<Long, Dish> implements DishDao {

    private static final Logger log = LogManager.getLogger(DishDaoImpl.class);

    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String PICTURE_COLUMN_NAME = "picture";
    private static final String CATEGORY_COLUMN_NAME = "category";
    private static final String PRICE_COLUMN_NAME = "price";
    private static final String DESCRIPTION_COLUMN_NAME = "description";

    private static final String FIND_ALL_SQL = """
            SELECT id, name, picture, category, price, description
            FROM dish""";
    private static final String FIND_BY_ID_SQL
            = FIND_ALL_SQL + WHERE_SQL + "id = ?";
    private static final String FIND_BY_NAME_LIKE_SQL
            = FIND_ALL_SQL + WHERE_SQL + "name ILIKE ?";
    private static final String FIND_BY_NAME_LIKE_OR_DESCRIPTION_LIKE_SQL
            = FIND_BY_NAME_LIKE_SQL + OR_SQL + "description ILIKE ?";
    private static final String FIND_BY_CATEGORY_SQL
            = FIND_ALL_SQL + WHERE_SQL + "category = ?::dish_category";
    private static final String CREATE_SQL = """
            INSERT INTO dish (name, picture, category, price, description)
            VALUES (?, ?, ?::dish_category, ?, ?)""";
    private static final String UPDATE_SQL = """
            UPDATE dish
            SET name        = ?,
                picture     = ?,
                category    = ?::dish_category,
                price       = ?,
                description = ?
            WHERE id = ?""";
    private static final String DELETE_SQL = """
            DELETE FROM dish
            WHERE id = ?""";

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
    public List<Dish> findByNameOrDescriptionLike(String str)
            throws DaoException {
        log.debug("Received string: {}", str);
        str = PERCENT + str + PERCENT;
        List<Dish> dishes = executeSelectQuery(
                FIND_BY_NAME_LIKE_OR_DESCRIPTION_LIKE_SQL, List.of(str, str));
        log.debug(RESULT_LOG_MESSAGE, dishes);
        return dishes;
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

        Path picturePath = Path.of(resultSet.getObject(
                PICTURE_COLUMN_NAME, String.class));
        log.trace("picturePath = {}", picturePath);

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
                .picturePath(picturePath)
                .category(category)
                .price(price)
                .description(description)
                .build();
    }

    private List<Object> createParamsList(Dish dish) {
        List<Object> params = new ArrayList<>();
        params.add(dish.getName());
        params.add(dish.getPicturePath().toString());
        params.add(dish.getCategory().toString());
        params.add(dish.getPrice());
        params.add(dish.getDescription());
        return params;
    }
}
