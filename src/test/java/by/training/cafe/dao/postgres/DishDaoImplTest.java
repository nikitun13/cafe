package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.pool.ConnectionPool;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.DishCategory;
import by.training.cafe.extension.DatabaseExtension;
import by.training.cafe.extension.SqlDaoParamResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({
        DatabaseExtension.class,
        SqlDaoParamResolver.class
})
class DishDaoImplTest {

    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String PICTURE_COLUMN_NAME = "picture";
    private static final String CATEGORY_COLUMN_NAME = "category";
    private static final String PRICE_COLUMN_NAME = "price";
    private static final String DESCRIPTION_COLUMN_NAME = "description";

    private static final String SET_UP_SQL = """
            INSERT INTO dish (id, name, picture, category, price, description)
            VALUES (1000000, 'Four seasons', 'pictures/four-seasons.png', 'PIZZA', 2000, 'Really delicious pizza!'),
                   (1000001, 'Chicken BBQ','pictures/chicken-bbq.png', 'PIZZA', 2500, 'Pizza with chicken and sauce BBQ'),
                   (1000002, 'Coca-Cola 1L','pictures/coca-cola.png', 'DRINKS', 200, 'Soft drink with caffeine and plant extracts.')""";
    private static final String TEAR_DOWN_SQL = "DELETE FROM dish";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM dish WHERE id = ?";
    private static final Connection connection = ConnectionPool.getInstance().getConnection();

    private static final Dish fourSeasons;
    private static final Dish chickenBbq;
    private static final Dish cocaCola;

    private final DishDaoImpl dishDao;
    private final Dish cocaColaClone;
    private final Dish napoleon;

    static {
        fourSeasons = Dish.builder()
                .id(1_000_000L)
                .name("Four seasons")
                .picturePath(Path.of("pictures/four-seasons.png"))
                .category(DishCategory.PIZZA)
                .price(2000L)
                .description("Really delicious pizza!")
                .build();

        chickenBbq = Dish.builder()
                .id(1_000_001L)
                .name("Chicken BBQ")
                .picturePath(Path.of("pictures/chicken-bbq.png"))
                .category(DishCategory.PIZZA)
                .description("Pizza with chicken and sauce BBQ")
                .price(2500L)
                .build();

        cocaCola = Dish.builder()
                .id(1_000_002L)
                .name("Coca-Cola 1L")
                .picturePath(Path.of("pictures/coca-cola.png"))
                .category(DishCategory.DRINKS)
                .price(200L)
                .description("Soft drink with caffeine and plant extracts.")
                .build();
    }

    DishDaoImplTest(DishDaoImpl dishDao) {
        this.dishDao = dishDao;

        cocaColaClone = Dish.builder()
                .id(1_000_002L)
                .name("Coca-Cola 1L")
                .picturePath(Path.of("pictures/coca-cola.png"))
                .category(DishCategory.DRINKS)
                .price(200L)
                .description("Soft drink with caffeine and plant extracts.")
                .build();

        napoleon = Dish.builder()
                .name("Napoleon")
                .picturePath(Path.of("pictures/napoleon.png"))
                .category(DishCategory.DESSERTS)
                .price(500L)
                .build();
    }

    public static Stream<Arguments> dataForFindById() {
        return Stream.of(
                Arguments.of(1_000_000L, Optional.of(fourSeasons)),
                Arguments.of(1_000_001L, Optional.of(chickenBbq)),
                Arguments.of(99_999_999L, Optional.empty())
        );
    }

    public static Stream<Arguments> dataForFindByNameLikeOrDescriptionLike() {
        return Stream.of(
                Arguments.of("pizza", List.of(fourSeasons, chickenBbq)),
                Arguments.of("Pizza", List.of(fourSeasons, chickenBbq)),
                Arguments.of("PIZZA", List.of(fourSeasons, chickenBbq)),
                Arguments.of("PiZzA", List.of(fourSeasons, chickenBbq)),
                Arguments.of("pizza", List.of(fourSeasons, chickenBbq)),
                Arguments.of("drink", List.of(cocaCola)),
                Arguments.of("bbq", List.of(chickenBbq)),
                Arguments.of("BBQ", List.of(chickenBbq)),
                Arguments.of("season", List.of(fourSeasons)),
                Arguments.of("and", List.of(chickenBbq, cocaCola)),
                Arguments.of("Really", List.of(fourSeasons)),
                Arguments.of("i", List.of(fourSeasons, chickenBbq, cocaCola))
        );
    }

    public static Stream<Arguments> dataForFindByCategory() {
        return Stream.of(
                Arguments.of(DishCategory.PIZZA.toString(), List.of(fourSeasons, chickenBbq)),
                Arguments.of(DishCategory.DRINKS.toString(), List.of(cocaCola)),
                Arguments.of(DishCategory.DESSERTS.toString(), Collections.emptyList())
        );
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SET_UP_SQL)) {
            statement.executeUpdate();
        }
    }

    @Test
    @Tag("findAll")
    void shouldReturnAllEntitiesFromDatabaseForFindAllMethod() throws DaoException {
        List<Dish> expected = List.of(fourSeasons, chickenBbq, cocaColaClone);

        List<Dish> actual = dishDao.findAll();

        assertEquals(expected, actual, () -> "Must return List: " + expected);
    }

    @Test
    @Tag("findAll")
    void shouldReturnEmptyListFromEmptyDatabaseForFindAllMethod() throws DaoException, SQLException {
        tearDown();
        List<Object> expected = Collections.emptyList();

        List<Dish> actual = dishDao.findAll();

        assertEquals(expected, actual, "Must return empty list");
    }

    @ParameterizedTest
    @MethodSource("dataForFindById")
    @Tag("findById")
    void shouldReturnEntityWithGivenIdIfPresentOrElseEmptyOptional(Long id,
                                                                   Optional<Dish> expected)
            throws DaoException {
        Optional<Dish> actual = dishDao.findById(id);

        assertEquals(expected, actual, () -> "expected to get: " + expected);
    }

    @Test
    @Tag("create")
    void shouldSetIdToCreatedDish() throws DaoException {
        Dish actual = napoleon;

        dishDao.create(actual);

        assertNotNull(actual.getId(), "must set id to created dish");
    }

    @Test
    @Tag("create")
    void shouldCreatedDishInTheDatabase() throws DaoException, SQLException {
        Dish expected = napoleon;

        dishDao.create(expected);
        Dish actual = findById(expected.getId());

        assertEquals(expected, actual, () -> "must create: " + expected);
    }

    @Test
    @Tag("update")
    void shouldUpdateEntityInDatabase() throws DaoException, SQLException {
        Dish expected = cocaColaClone;
        expected.setName("Coca-cola zero");
        expected.setDescription("No calories");
        expected.setPrice(500L);

        boolean isUpdated = dishDao.update(expected);
        Dish actual = findById(expected.getId());
        assertAll(
                () -> assertTrue(isUpdated, "must return true if dish was updated in database"),
                () -> assertEquals(expected, actual, () -> "must update in database to: " + expected)
        );
    }

    @Test
    @Tag("update")
    void shouldReturnFalseIfDishWasNotUpdated() throws DaoException {
        cocaColaClone.setId(-1L);
        boolean isUpdated = dishDao.update(cocaColaClone);

        assertFalse(isUpdated, "mustn't update any dishes if no such dish id");
    }

    @Test
    @Tag("delete")
    void shouldDeleteEntityFromDatabase() throws DaoException, SQLException {
        Dish expected = cocaColaClone;

        boolean isDeleted = dishDao.delete(expected.getId());
        Dish actual = findById(expected.getId());

        assertAll(
                () -> assertTrue(isDeleted, "must return true if dish was deleted from database"),
                () -> assertNull(actual, () -> "must delete %s from database".formatted(expected))
        );
    }

    @Test
    @Tag("delete")
    void shouldReturnFalseIfDishWasNotDeleted() throws DaoException {
        boolean isDeleted = dishDao.delete(-1L);

        assertFalse(isDeleted, "mustn't delete any dishes if no such dish id");
    }

    @ParameterizedTest
    @MethodSource("dataForFindByNameLikeOrDescriptionLike")
    @Tag("findByNameLikeOrDescriptionLike")
    void shouldReturnListOfDishesWhichNameOrDescriptionContainsGivenString(String str,
                                                                           List<Dish> expected)
            throws DaoException {
        List<Dish> actual = dishDao.findByNameOrDescriptionLike(str);

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @ParameterizedTest
    @MethodSource("dataForFindByCategory")
    @Tag("findByCategory")
    void shouldReturnListOfDishesWithGivenCategory(String category, List<Dish> expected) throws DaoException {
        List<Dish> actual = dishDao.findByCategory(category);

        assertEquals(expected, actual, () -> "Must return list: " + expected);
    }

    @Test
    @Tag("findByCategory")
    void shouldThrowExceptionIfThereIsNoSuchCategoryForNoSuchCategoryMethod() {
        assertThrows(DaoException.class,
                () -> dishDao.findByCategory("no_such_category"),
                () -> "Must throw %s if there is no such category".formatted(DaoException.class.getName()));
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(TEAR_DOWN_SQL)) {
            statement.executeUpdate();
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

    private Dish findById(Long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Dish.builder()
                        .id(resultSet.getObject(ID_COLUMN_NAME, Long.class))
                        .name(resultSet.getObject(NAME_COLUMN_NAME, String.class))
                        .picturePath(Path.of(resultSet.getObject(PICTURE_COLUMN_NAME, String.class)))
                        .category(DishCategory.valueOf(resultSet.getObject(CATEGORY_COLUMN_NAME, String.class)))
                        .price(resultSet.getObject(PRICE_COLUMN_NAME, Long.class))
                        .description(resultSet.getObject(DESCRIPTION_COLUMN_NAME, String.class))
                        .build();
            }
        }
        return null;
    }
}
