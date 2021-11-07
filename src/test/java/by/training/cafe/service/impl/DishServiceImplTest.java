package by.training.cafe.service.impl;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.DishDao;
import by.training.cafe.dao.Transaction;
import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dto.DishDto;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.DishCategory;
import by.training.cafe.service.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceImplTest {

    private static final Dish FOUR_SEASONS_DISH;
    private static final Dish CHICKEN_BBQ_DISH;
    private static final Dish COCA_COLA_DISH;
    private static final DishDto FOUR_SEASONS_DTO;
    private static final DishDto CHICKEN_BBQ_DTO;
    private static final DishDto COCA_COLA_DTO;

    static {
        FOUR_SEASONS_DISH = Dish.builder()
                .id(1_000_000L)
                .name("Four seasons")
                .category(DishCategory.PIZZA)
                .price(2000L)
                .description("Really delicious pizza!")
                .build();
        FOUR_SEASONS_DTO = DishDto.builder()
                .id(1_000_000L)
                .name("Four seasons")
                .category("Pizza")
                .price(2000L)
                .description("Really delicious pizza!")
                .build();

        CHICKEN_BBQ_DISH = Dish.builder()
                .id(1_000_001L)
                .name("Chicken BBQ")
                .category(DishCategory.PIZZA)
                .description("Pizza with chicken and sauce BBQ")
                .price(2500L)
                .build();
        CHICKEN_BBQ_DTO = DishDto.builder()
                .id(1_000_001L)
                .name("Chicken BBQ")
                .category("Pizza")
                .description("Pizza with chicken and sauce BBQ")
                .price(2500L)
                .build();

        COCA_COLA_DISH = Dish.builder()
                .id(1_000_002L)
                .name("Coca-Cola 1L")
                .category(DishCategory.DRINKS)
                .price(200L)
                .description("Soft drink with caffeine and plant extracts.")
                .build();
        COCA_COLA_DTO = DishDto.builder()
                .id(1_000_002L)
                .name("Coca-Cola 1L")
                .category("Drinks")
                .price(200L)
                .description("Soft drink with caffeine and plant extracts.")
                .build();
    }

    private final Dish cocaColaCloneDish;
    private final DishDto cocaColaCloneDto;
    private final Dish napoleonDish;
    private final DishDto napoleonDto;

    {
        cocaColaCloneDish = Dish.builder()
                .id(1_000_002L)
                .name("Coca-Cola 1L")
                .category(DishCategory.DRINKS)
                .price(200L)
                .description("Soft drink with caffeine and plant extracts.")
                .build();
        cocaColaCloneDto = DishDto.builder()
                .id(1_000_002L)
                .name("Coca-Cola 1L")
                .category("Drinks")
                .price(200L)
                .description("Soft drink with caffeine and plant extracts.")
                .build();

        napoleonDish = Dish.builder()
                .name("Napoleon")
                .category(DishCategory.DESSERTS)
                .price(500L)
                .description("napoleon")
                .build();
        napoleonDto = DishDto.builder()
                .name("Napoleon")
                .category("Desserts")
                .price(500L)
                .description("napoleon")
                .build();
    }

    @Mock(lenient = true)
    private TransactionFactory transactionFactory;
    @Mock(lenient = true)
    private Transaction transaction;
    @Mock
    private DishDao dishDao;
    @InjectMocks
    private DishServiceImpl service;

    public static Stream<Arguments> invalidDataForFindByIdMethod() {
        return Stream.of(
                Arguments.of((Long) null),
                Arguments.of(0L),
                Arguments.of(-1L),
                Arguments.of(-2L),
                Arguments.of(-500L),
                Arguments.of(-1000000L)
        );
    }

    public static Stream<Arguments> invalidDataForUpdateMethod() {
        return Stream.of(
                Arguments.of((DishDto) null),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Drinks")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).category("Drinks").price(200L)
                        .description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Drinks")
                        .price(200L).build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Drinksw")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Pizzas")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Pizzza")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Drinks")
                        .price(-1L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Drinks")
                        .price(0L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("           ").category("Drinks")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category(" ")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Drinks")
                        .price(200L).description("   ").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).category("Drinks").price(200L).build()),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Drinks").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).price(200L)
                        .description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(1_000_002L).category("Drinks")
                        .description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().build())
        );
    }

    public static Stream<Arguments> invalidDataForCreateMethod() {
        return Stream.of(
                Arguments.of((DishDto) null),
                Arguments.of(DishDto.builder().id(1_000_002L).name("Coca-Cola 1L").category("Drinks")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().category("Drinks").price(200L)
                        .description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Drinks")
                        .price(200L).build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Drinksw")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Pizzas")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Pizzza")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Drinks")
                        .price(-1L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Drinks")
                        .price(0L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("           ").category("Drinks")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category(" ")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Drinks")
                        .price(200L).description("   ").build()),
                Arguments.of(DishDto.builder().category("Drinks").price(200L).build()),
                Arguments.of(DishDto.builder().name("Coca-Cola 1L").category("Drinks").build()),
                Arguments.of(DishDto.builder().price(200L)
                        .description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().category("Drinks")
                        .description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().build())
        );
    }

    public static Stream<Arguments> invalidDataForDeleteMethod() {
        return Stream.of(
                Arguments.of((DishDto) null),
                Arguments.of(DishDto.builder().id(0L).name("Coca-Cola 1L").category("Drinks")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(-1L).name("Coca-Cola 1L").category("Drinks")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().id(-555L).name("Coca-Cola 1L").category("Drinks")
                        .price(200L).description("Soft drink with caffeine and plant extracts.").build()),
                Arguments.of(DishDto.builder().build())
        );
    }

    public static Stream<Arguments> invalidDataForFindByCategory() {
        return Stream.of(
                Arguments.of("    "),
                Arguments.of("  PIZZA  "),
                Arguments.of("  pizza  "),
                Arguments.of("pizza  "),
                Arguments.of("    pizza"),
                Arguments.of(" pizza"),
                Arguments.of("pizza "),
                Arguments.of("PIZZA "),
                Arguments.of("noSuchCategory")
        );
    }

    @BeforeEach
    void setUp() {
        doReturn(dishDao).when(transaction).createDao(DishDao.class);
        doReturn(transaction).when(transactionFactory).createTransaction();
    }

    @Test
    @Tag("findAll")
    void shouldMapAllEntitiesFromDbToDto() throws DaoException, ServiceException {
        doReturn(List.of(FOUR_SEASONS_DISH, CHICKEN_BBQ_DISH, COCA_COLA_DISH))
                .when(dishDao).findAll();
        List<DishDto> expected = List.of(FOUR_SEASONS_DTO, CHICKEN_BBQ_DTO, COCA_COLA_DTO);

        List<DishDto> actual = service.findAll();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("findAll")
    void shouldReturnEmptyListIfNoEntitiesInDb() throws DaoException, ServiceException {
        doReturn(Collections.emptyList())
                .when(dishDao)
                .findAll();
        List<DishDto> expected = Collections.emptyList();

        List<DishDto> actual = service.findAll();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("findAll")
    void shouldThrowExceptionIfDatabaseIsDisconnected() throws DaoException {
        doThrow(DaoException.class)
                .when(dishDao)
                .findAll();

        assertThatThrownBy(() -> service.findAll()).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("findById")
    void shouldReturnDtoById() throws DaoException, ServiceException {
        doReturn(Optional.of(COCA_COLA_DISH))
                .when(dishDao)
                .findById(COCA_COLA_DISH.getId());

        Optional<DishDto> actual = service.findById(COCA_COLA_DISH.getId());

        assertThat(actual).isNotEmpty().contains(COCA_COLA_DTO);
    }

    @Test
    @Tag("findById")
    void shouldReturnEmptyOptionalIfNoSuchId() throws DaoException, ServiceException {
        doReturn(Optional.empty())
                .when(dishDao)
                .findById(1L);

        Optional<DishDto> actual = service.findById(1L);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidDataForFindByIdMethod")
    @Tag("findById")
    void shouldThrowExceptionIfIdIsInvalid(Long id) {
        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("create")
    void shouldCreateDishAndSetIdToDto() throws DaoException, ServiceException {
        doAnswer(
                invocation -> {
                    Dish dish = invocation.getArgument(0);
                    dish.setId(100L);
                    return null;
                }).when(dishDao)
                .create(napoleonDish);

        service.create(napoleonDto);

        assertThat(napoleonDto.getId()).isNotNull().isEqualTo(100L);
    }

    @ParameterizedTest
    @MethodSource("invalidDataForCreateMethod")
    @Tag("create")
    void shouldThrowExceptionIfReceivedInvalidDtoForCreateMethod(DishDto dto) {
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("update")
    void shouldReturnTrueIfValidEntityUpdated() throws DaoException, ServiceException {
        cocaColaCloneDto.setName("Coca-Cola Zero");
        cocaColaCloneDto.setPrice(500L);

        cocaColaCloneDish.setName("Coca-Cola Zero");
        cocaColaCloneDish.setPrice(500L);
        doReturn(true).when(dishDao).update(cocaColaCloneDish);

        boolean actual = service.update(cocaColaCloneDto);

        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidDataForUpdateMethod")
    @Tag("update")
    void shouldThrowExceptionIfReceivedInvalidDtoForUpdateMethod(DishDto dto) {
        assertThatThrownBy(() -> service.update(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("delete")
    void shouldReturnTrueIfValidEntityDeleted() throws DaoException, ServiceException {
        doReturn(true).when(dishDao).delete(CHICKEN_BBQ_DISH.getId());

        boolean actual = service.delete(CHICKEN_BBQ_DTO);

        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidDataForDeleteMethod")
    @Tag("delete")
    void shouldThrowExceptionIfReceivedInvalidDtoForDeleteMethod(DishDto dto) {
        assertThatThrownBy(() -> service.delete(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("findAllGroupByCategory")
    void shouldMapToEntityAndGroupByCategory() throws DaoException, ServiceException {
        doReturn(List.of(FOUR_SEASONS_DISH, CHICKEN_BBQ_DISH, COCA_COLA_DISH))
                .when(dishDao).findAll();
        Map<String, List<DishDto>> expected = Map.of(
                "Pizza", List.of(FOUR_SEASONS_DTO, CHICKEN_BBQ_DTO),
                "Drinks", List.of(COCA_COLA_DTO));

        Map<String, List<DishDto>> actual = service.findAllGroupByCategory();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("findByNameOrDescriptionLike")
    void shouldMapResultToEntity() throws DaoException, ServiceException {
        List<DishDto> expected = List.of(FOUR_SEASONS_DTO, CHICKEN_BBQ_DTO);
        doReturn(List.of(FOUR_SEASONS_DISH, CHICKEN_BBQ_DISH))
                .when(dishDao)
                .findByNameOrDescriptionLike("pizza");

        List<DishDto> actual = service.findByNameOrDescriptionLike("pizza");

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @Tag("findByNameOrDescriptionLike")
    void shouldThrowExceptionIfInvalidStringReceived(String str) {
        assertThatThrownBy(() -> service.findByNameOrDescriptionLike(str)).isInstanceOf(ServiceException.class);

    }

    @Test
    @Tag("findByCategory")
    void shouldMapFoundEntitiesToDtoForFindByCategoryUsingLowercaseCategory() throws DaoException, ServiceException {
        List<DishDto> expected = List.of(COCA_COLA_DTO);
        doReturn(List.of(COCA_COLA_DISH))
                .when(dishDao)
                .findByCategory("DRINKS");

        List<DishDto> actual = service.findByCategory("drinks");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("findByCategory")
    void shouldMapFoundEntitiesToDtoForFindByCategory() throws DaoException, ServiceException {
        List<DishDto> expected = List.of(FOUR_SEASONS_DTO, CHICKEN_BBQ_DTO);
        doReturn(List.of(FOUR_SEASONS_DISH, CHICKEN_BBQ_DISH))
                .when(dishDao)
                .findByCategory("PIZZA");

        List<DishDto> actual = service.findByCategory("PIZZA");

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @MethodSource("invalidDataForFindByCategory")
    @Tag("findByCategory")
    void shouldThrowExceptionIfInvalidCategoryReceived(String category) {
        assertThatThrownBy(() -> service.findByCategory(category)).isInstanceOf(ServiceException.class);
    }
}
