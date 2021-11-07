package by.training.cafe.dao.postgres;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.pool.ConnectionPool;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.DishCategory;
import by.training.cafe.extension.DatabaseExtension;
import by.training.cafe.extension.SqlDaoParamResolver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
    private static final String CATEGORY_COLUMN_NAME = "category";
    private static final String PRICE_COLUMN_NAME = "price";
    private static final String DESCRIPTION_COLUMN_NAME = "description";

    private static final String SET_UP_SQL = """
            INSERT INTO dish (id, name, category, price, description)
            VALUES (1000000, 'Four seasons', 'PIZZA', 2000, 'Really delicious pizza!'),
                   (1000001, 'Chicken BBQ', 'PIZZA', 2500, 'Pizza with chicken and sauce BBQ'),
                   (1000002, 'Coca-Cola 1L', 'DRINKS', 200, 'Soft drink with caffeine and plant extracts.')""";
    private static final String TEAR_DOWN_SQL = "DELETE FROM dish";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM dish WHERE id = ?";
    private static final Connection CONNECTION = ConnectionPool.getInstance().getConnection();

    private static final Dish FOUR_SEASONS;
    private static final Dish CHICKEN_BBQ;
    private static final Dish COCA_COLA;

    private final DishDaoImpl dishDao;
    private final Dish cocaColaClone;
    private final Dish napoleon;

    static {
        FOUR_SEASONS = Dish.builder()
                .id(1_000_000L)
                .name("Four seasons")
                .category(DishCategory.PIZZA)
                .price(2000L)
                .description("Really delicious pizza!")
                .build();

        CHICKEN_BBQ = Dish.builder()
                .id(1_000_001L)
                .name("Chicken BBQ")
                .category(DishCategory.PIZZA)
                .description("Pizza with chicken and sauce BBQ")
                .price(2500L)
                .build();

        COCA_COLA = Dish.builder()
                .id(1_000_002L)
                .name("Coca-Cola 1L")
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
                .category(DishCategory.DRINKS)
                .price(200L)
                .description("Soft drink with caffeine and plant extracts.")
                .build();

        napoleon = Dish.builder()
                .name("Napoleon")
                .category(DishCategory.DESSERTS)
                .price(500L)
                .description("napoleon")
                .build();
    }

    public static Stream<Arguments> dataForFindById() {
        return Stream.of(
                Arguments.of(1_000_000L, Optional.of(FOUR_SEASONS)),
                Arguments.of(1_000_001L, Optional.of(CHICKEN_BBQ)),
                Arguments.of(99_999_999L, Optional.empty())
        );
    }

    public static Stream<Arguments> dataForFindByNameLikeOrDescriptionLike() {
        return Stream.of(
                Arguments.of("pizza", List.of(FOUR_SEASONS, CHICKEN_BBQ)),
                Arguments.of("Pizza", List.of(FOUR_SEASONS, CHICKEN_BBQ)),
                Arguments.of("PIZZA", List.of(FOUR_SEASONS, CHICKEN_BBQ)),
                Arguments.of("PiZzA", List.of(FOUR_SEASONS, CHICKEN_BBQ)),
                Arguments.of("pizza", List.of(FOUR_SEASONS, CHICKEN_BBQ)),
                Arguments.of("                  pizza              ", List.of(FOUR_SEASONS, CHICKEN_BBQ)),
                Arguments.of("                  piz za              ", List.of(FOUR_SEASONS, CHICKEN_BBQ)),
                Arguments.of("        Really          pizza              ", List.of(FOUR_SEASONS)),
                Arguments.of("really pizza", List.of(FOUR_SEASONS)),
                Arguments.of("drink", List.of(COCA_COLA)),
                Arguments.of("bbq", List.of(CHICKEN_BBQ)),
                Arguments.of("BBQ", List.of(CHICKEN_BBQ)),
                Arguments.of("season", List.of(FOUR_SEASONS)),
                Arguments.of("and", List.of(CHICKEN_BBQ, COCA_COLA)),
                Arguments.of("Really", List.of(FOUR_SEASONS)),
                Arguments.of("i", List.of(FOUR_SEASONS, CHICKEN_BBQ, COCA_COLA))
        );
    }

    public static Stream<Arguments> dataForFindByCategory() {
        return Stream.of(
                Arguments.of(DishCategory.PIZZA.toString(), List.of(FOUR_SEASONS, CHICKEN_BBQ)),
                Arguments.of(DishCategory.DRINKS.toString(), List.of(COCA_COLA)),
                Arguments.of(DishCategory.DESSERTS.toString(), Collections.emptyList())
        );
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (PreparedStatement statement = CONNECTION.prepareStatement(SET_UP_SQL)) {
            statement.executeUpdate();
        }
    }

    @Test
    @Tag("findAll")
    void shouldReturnAllDishesFromDatabaseForFindAllMethod() throws DaoException {
        List<Dish> expected = List.of(FOUR_SEASONS, CHICKEN_BBQ, cocaColaClone);

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
    void shouldReturnDishWithGivenIdIfPresentOrElseEmptyOptional(Long id,
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
    void shouldUpdateDishInDatabase() throws DaoException, SQLException {
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
    void shouldDeleteDishFromDatabase() throws DaoException, SQLException {
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

        Assertions.assertThat(actual).hasSameSizeAs(expected).hasSameElementsAs(expected);
    }

    @ParameterizedTest
    @MethodSource("dataForFindByCategory")
    @Tag("findByCategory")
    void shouldReturnListOfDishesWithGivenCategory(String category, List<Dish> expected) throws DaoException {
        List<Dish> actual = dishDao.findByCategory(category);

        assertIterableEquals(expected, actual, () -> "Must return list: " + expected);
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

    private Dish findById(Long id) throws SQLException {
        try (PreparedStatement statement = CONNECTION.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Dish.builder()
                        .id(resultSet.getObject(ID_COLUMN_NAME, Long.class))
                        .name(resultSet.getObject(NAME_COLUMN_NAME, String.class))
                        .category(DishCategory.valueOf(resultSet.getObject(CATEGORY_COLUMN_NAME, String.class)))
                        .price(resultSet.getObject(PRICE_COLUMN_NAME, Long.class))
                        .description(resultSet.getObject(DESCRIPTION_COLUMN_NAME, String.class))
                        .build();
            }
        }
        return null;
    }
}
