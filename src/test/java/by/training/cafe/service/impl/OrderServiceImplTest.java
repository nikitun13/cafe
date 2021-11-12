package by.training.cafe.service.impl;

import by.training.cafe.dao.*;
import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderStatus;
import by.training.cafe.entity.User;
import by.training.cafe.entity.UserRole;
import by.training.cafe.service.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private static final User IVAN;
    private static final User PETR;
    private static final User JOHN;

    private static final UserDto IVAN_DTO;
    private static final UserDto PETR_DTO;
    private static final UserDto JOHN_DTO;

    private static final Order IVAN_FIRST_ORDER;
    private static final Order IVAN_SECOND_ORDER;
    private static final Order JOHN_ORDER;

    private static final OrderDto IVAN_FIRST_ORDER_DTO;
    private static final OrderDto IVAN_SECOND_ORDER_DTO;
    private static final OrderDto JOHN_ORDER_DTO;

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

        IVAN_DTO = UserDto.builder()
                .id(1_000_000L)
                .email("ivan@gmail.com")
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
                .role(UserRole.CLIENT)
                .firstName("Петр")
                .lastName("Шариков")
                .phone("+375442222222")
                .points(130L)
                .isBlocked(false)
                .build();

        PETR_DTO = UserDto.builder()
                .id(1_000_001L)
                .email("petr@mail.ru")
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

        JOHN_DTO = UserDto.builder()
                .id(1_000_002L)
                .email("john@gmail.com")
                .role(UserRole.ADMIN)
                .firstName("John")
                .lastName("Henson")
                .phone("+375333333333")
                .points(270L)
                .isBlocked(false)
                .build();


        IVAN_FIRST_ORDER = Order.builder()
                .id(3000000L)
                .user(User.builder().id(1000000L).build())
                .createdAt(Timestamp.valueOf("2021-11-04 15:35:36"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-04 17:35:36"))
                .actualRetrieveDate(Timestamp.valueOf("2021-11-04 17:27:36"))
                .status(OrderStatus.COMPLETED)
                .accruedPoints(10L)
                .debitedPoints(100L)
                .totalPrice(3000L)
                .build();

        IVAN_FIRST_ORDER_DTO = OrderDto.builder()
                .id(3000000L)
                .user(IVAN_DTO)
                .createdAt(Timestamp.valueOf("2021-11-04 15:35:36"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-04 17:35:36"))
                .actualRetrieveDate(Timestamp.valueOf("2021-11-04 17:27:36"))
                .status("Completed")
                .accruedPoints(10L)
                .debitedPoints(100L)
                .totalPrice(3000L)
                .build();

        IVAN_SECOND_ORDER = Order.builder()
                .id(3000001L)
                .user(User.builder().id(1000000L).build())
                .createdAt(Timestamp.valueOf("2021-11-04 17:52:19"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-05 15:52:19"))
                .status(OrderStatus.CANCELED)
                .accruedPoints(0L)
                .debitedPoints(0L)
                .totalPrice(400L)
                .build();

        IVAN_SECOND_ORDER_DTO = OrderDto.builder()
                .id(3000001L)
                .user(IVAN_DTO)
                .createdAt(Timestamp.valueOf("2021-11-04 17:52:19"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-05 15:52:19"))
                .status("Canceled")
                .accruedPoints(0L)
                .debitedPoints(0L)
                .totalPrice(400L)
                .build();

        JOHN_ORDER = Order.builder()
                .id(3000002L)
                .user(JOHN)
                .createdAt(Timestamp.valueOf("2021-11-05 13:00:00"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-10 13:00:00"))
                .status(OrderStatus.PENDING)
                .accruedPoints(300L)
                .debitedPoints(20L)
                .totalPrice(4000L)
                .build();

        JOHN_ORDER_DTO = OrderDto.builder()
                .id(3000002L)
                .user(JOHN_DTO)
                .createdAt(Timestamp.valueOf("2021-11-05 13:00:00"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-10 13:00:00"))
                .status("Pending")
                .accruedPoints(300L)
                .debitedPoints(20L)
                .totalPrice(4000L)
                .build();
    }

    private final Order petrOrder;
    private final OrderDto petrOrderDto;
    private final CreateOrderDto petrCreateOrderDto;

    {
        petrOrder = Order.builder()
                .user(PETR)
                .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                .status(OrderStatus.PENDING)
                .accruedPoints(0L)
                .debitedPoints(90L)
                .totalPrice(2500L)
                .build();

        petrOrderDto = OrderDto.builder()
                .id(3000003L)
                .user(PETR_DTO)
                .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                .status("Pending")
                .accruedPoints(0L)
                .debitedPoints(90L)
                .totalPrice(2500L)
                .build();

        petrCreateOrderDto = CreateOrderDto.builder()
                .user(PETR_DTO)
                .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                .debitedPoints(90L)
                .totalPrice(2500L)
                .build();
    }

    @Mock(lenient = true)
    private TransactionFactory transactionFactory;
    @Mock(lenient = true)
    private Transaction transaction;
    @Mock
    private UserDao userDao;
    @Mock
    private OrderDao orderDao;
    @InjectMocks
    private OrderServiceImpl service;

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

    public static Stream<Arguments> invalidDataForCreateMethod() {
        return Stream.of(
                Arguments.of((CreateOrderDto) null),
                Arguments.of(CreateOrderDto.builder()
                        .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                        .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                        .debitedPoints(90L)
                        .totalPrice(2500L)
                        .build()),
                Arguments.of(CreateOrderDto.builder()
                        .user(PETR_DTO)
                        .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                        .debitedPoints(90L)
                        .totalPrice(2500L)
                        .build()),
                Arguments.of(CreateOrderDto.builder()
                        .user(PETR_DTO)
                        .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                        .debitedPoints(90L)
                        .totalPrice(2500L)
                        .build()),
                Arguments.of(CreateOrderDto.builder()
                        .user(PETR_DTO)
                        .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                        .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                        .totalPrice(2500L)
                        .build()),
                Arguments.of(CreateOrderDto.builder()
                        .user(PETR_DTO)
                        .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                        .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                        .debitedPoints(90L)
                        .build()),
                Arguments.of(CreateOrderDto.builder().build()),
                Arguments.of(CreateOrderDto.builder()
                        .user(UserDto.builder().build())
                        .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                        .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                        .debitedPoints(90L)
                        .totalPrice(2500L)
                        .build()),
                Arguments.of(CreateOrderDto.builder()
                        .user(UserDto.builder().id(500L).build())
                        .createdAt(Timestamp.valueOf("2021-11-06 10:12:36"))
                        .expectedRetrieveDate(Timestamp.valueOf("2021-11-06 15:12:36"))
                        .debitedPoints(90L)
                        .totalPrice(2500L)
                        .build())
        );
    }

    public static Stream<Arguments> invalidOrderDtos() {
        return Stream.of(Arguments.of());
    }

    @BeforeEach
    void setUp() {
        doReturn(userDao).when(transaction).createDao(UserDao.class);
        doReturn(orderDao).when(transaction).createDao(OrderDao.class);
        doReturn(transaction).when(transactionFactory).createTransaction();
    }

    @Test
    @Tag("findAll")
    void shouldMapAllOrdersFromDbToDto() throws DaoException, ServiceException {
        List<OrderDto> expected = List.of(IVAN_FIRST_ORDER_DTO, IVAN_SECOND_ORDER_DTO, JOHN_ORDER_DTO);
        doReturn(List.of(IVAN_FIRST_ORDER, IVAN_SECOND_ORDER, JOHN_ORDER))
                .when(orderDao)
                .findAll();
        doReturn(Optional.of(IVAN))
                .when(userDao)
                .findById(IVAN.getId());
        doReturn(Optional.of(JOHN))
                .when(userDao)
                .findById(JOHN.getId());

        List<OrderDto> actual = service.findAll();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("findById")
    void shouldReturnOrderDtoById() throws DaoException, ServiceException {
        doReturn(Optional.of(JOHN_ORDER))
                .when(orderDao)
                .findById(JOHN_ORDER.getId());
        doReturn(Optional.of(JOHN))
                .when(userDao)
                .findById(JOHN_ORDER.getUser().getId());

        Optional<OrderDto> actual = service.findById(JOHN_ORDER_DTO.getId());

        assertThat(actual).isNotEmpty().contains(JOHN_ORDER_DTO);
    }

    @Test
    @Tag("findById")
    void shouldReturnEmptyOptionalIfNoSuchId() throws DaoException, ServiceException {
        doReturn(Optional.empty())
                .when(orderDao)
                .findById(1L);

        Optional<OrderDto> actual = service.findById(1L);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidDataForFindByIdMethod")
    @Tag("findById")
    void shouldThrowExceptionIfIdIsInvalid(Long id) {
        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("create")
    void shouldCreateOrderFromCreateOrderDtoAndGetIdAndMapToOrderDto() throws DaoException, ServiceException {
        doAnswer(
                invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(3000003L);
                    return null;
                }).when(orderDao)
                .create(petrOrder);

        OrderDto actual = service.create(petrCreateOrderDto);

        assertThat(actual).isEqualTo(petrOrderDto);
    }

    @ParameterizedTest
    @MethodSource("invalidDataForCreateMethod")
    @Tag("create")
    void shouldThrowExceptionIfReceivedInvalidDtoForCreateMethod(CreateOrderDto dto) {
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("update")
    void shouldReturnTrueIfValidEntityUpdated() throws DaoException, ServiceException {
        petrOrderDto.setId(3000003L);
        petrOrder.setId(3000003L);
        doReturn(true)
                .when(orderDao)
                .update(petrOrder);

        boolean actual = service.update(petrOrderDto);

        assertThat(actual).isTrue();
    }

    @Disabled("Not implemented")
    @ParameterizedTest
    @MethodSource("invalidOrderDtos")
    @Tag("update")
    void shouldThrowExceptionIfReceivedInvalidDtoForUpdateMethod(OrderDto dto) {
        assertThatThrownBy(() -> service.update(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("delete")
    void shouldReturnTrueIfValidEntityDeleted() throws DaoException, ServiceException {
        doReturn(true).when(orderDao).delete(IVAN_FIRST_ORDER.getId());

        boolean actual = service.delete(IVAN_FIRST_ORDER_DTO);

        assertThat(actual).isTrue();
    }

    @Disabled("Not implemented")
    @ParameterizedTest
    @MethodSource("invalidOrderDtos")
    @Tag("delete")
    void shouldThrowExceptionIfReceivedInvalidDtoForDeleteMethod(OrderDto dto) {
        assertThatThrownBy(() -> service.delete(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("findByUserDto")
    void shouldReturnAllOrdersByGivenUser() throws DaoException, ServiceException {
        List<OrderDto> expected = List.of(IVAN_FIRST_ORDER_DTO, IVAN_SECOND_ORDER_DTO);
        doReturn(List.of(IVAN_FIRST_ORDER, IVAN_SECOND_ORDER))
                .when(orderDao)
                .findByUserId(IVAN.getId());

        List<OrderDto> actual = service.findByUserDto(IVAN_DTO);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("findByCreatedAtBetween")
    void findByCreatedAtBetween() throws DaoException, ServiceException {
        List<OrderDto> expected = List.of(IVAN_FIRST_ORDER_DTO, IVAN_SECOND_ORDER_DTO, JOHN_ORDER_DTO);
        doReturn(List.of(IVAN_FIRST_ORDER, IVAN_SECOND_ORDER, JOHN_ORDER))
                .when(orderDao)
                .findByCreatedAtBetween(
                        Timestamp.valueOf("2021-11-04 00:00:00"),
                        Timestamp.valueOf("2021-11-06 00:00:00"));
        doReturn(Optional.of(IVAN))
                .when(userDao)
                .findById(IVAN.getId());
        doReturn(Optional.of(JOHN))
                .when(userDao)
                .findById(JOHN.getId());

        List<OrderDto> actual = service.findByCreatedAtBetween(
                Timestamp.valueOf("2021-11-04 00:00:00"),
                Timestamp.valueOf("2021-11-06 00:00:00"));

        assertThat(actual).isEqualTo(expected);
    }
}
