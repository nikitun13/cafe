package by.training.cafe.service.impl;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.Transaction;
import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dao.UserDao;
import by.training.cafe.dto.CreateUserDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.User;
import by.training.cafe.entity.UserRole;
import by.training.cafe.service.EncoderService;
import by.training.cafe.service.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final User IVAN;
    private static final User PETR;
    private static final User JOHN;

    private static final UserDto IVAN_DTO;
    private static final UserDto PETR_DTO;
    private static final UserDto JOHN_DTO;

    static {
        IVAN = User.builder()
                .id(1_000_000L)
                .email("ivan@gmail.com")
                .password("$2a$10$.IVAN.PASS.43WRAZ3Fnyx.C/6PveEHf6JGzGo9X2SQSwM5djXdrO")
                .role(UserRole.CLIENT)
                .firstName("Ivan")
                .lastName("Melnikov")
                .phone("375251111111")
                .points(30L)
                .isBlocked(true)
                .build();

        IVAN_DTO = UserDto.builder()
                .id(1_000_000L)
                .email("ivan@gmail.com")
                .role(UserRole.CLIENT)
                .firstName("Ivan")
                .lastName("Melnikov")
                .phone("375251111111")
                .points(30L)
                .isBlocked(true)
                .build();

        PETR = User.builder()
                .id(1_000_001L)
                .email("petr@mail.ru")
                .password("$2a$10$RJXf1EauSdF9rOyCkeT49O3FF9idJIlRAPvn8X5UrX6lL6/i0JZ1y")
                .role(UserRole.CLIENT)
                .firstName("Петр")
                .lastName("Шариков")
                .phone("375442222222")
                .points(130L)
                .isBlocked(false)
                .build();

        PETR_DTO = UserDto.builder()
                .id(1_000_001L)
                .email("petr@mail.ru")
                .role(UserRole.CLIENT)
                .firstName("Петр")
                .lastName("Шариков")
                .phone("375442222222")
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
                .phone("375333333333")
                .points(270L)
                .isBlocked(false)
                .build();

        JOHN_DTO = UserDto.builder()
                .id(1_000_002L)
                .email("john@gmail.com")
                .role(UserRole.ADMIN)
                .firstName("John")
                .lastName("Henson")
                .phone("375333333333")
                .points(270L)
                .isBlocked(false)
                .build();
    }

    private final User hans;
    private final User adam;

    private final CreateUserDto hansCreateDto;
    private final UserDto hansDto;
    private final UserDto adamDto;


    {
        hans = User.builder()
                .email("hans@gmail.com")
                .password("$2a$10$.Hans.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr2")
                .role(UserRole.CLIENT)
                .firstName("Hans")
                .lastName("Münz")
                .phone("375254444444")
                .points(0L)
                .isBlocked(false)
                .build();

        hansDto = UserDto.builder()
                .id(1_000_003L)
                .email("hans@gmail.com")
                .role(UserRole.CLIENT)
                .firstName("Hans")
                .lastName("Münz")
                .phone("375254444444")
                .points(0L)
                .isBlocked(false)
                .build();

        hansCreateDto = CreateUserDto.builder()
                .email("hans@gmail.com")
                .password("qwerty123")
                .repeatPassword("qwerty123")
                .firstName("haNs")
                .lastName("MüNz")
                .phone("375254444444")
                .build();

        adam = User.builder()
                .id(800_000L)
                .email("smith@gmail.com")
                .role(UserRole.CLIENT)
                .firstName("Adam")
                .lastName("Smith")
                .phone("375445555555")
                .points(500L)
                .isBlocked(false)
                .build();

        adamDto = UserDto.builder()
                .id(800_000L)
                .email("smith@gmail.com")
                .role(UserRole.CLIENT)
                .firstName("Adam")
                .lastName("Smith")
                .phone("375445555555")
                .points(500L)
                .isBlocked(false)
                .build();
    }

    @Mock(lenient = true)
    private TransactionFactory transactionFactory;
    @Mock(lenient = true)
    private Transaction transaction;
    @Mock
    private UserDao userDao;
    @Mock
    private EncoderService encoderService;
    @InjectMocks
    private UserServiceImpl service;

    public static Stream<Arguments> invalidDataForSignIn() {
        return Stream.of(
                Arguments.of(null, "validPass123"),
                Arguments.of("valid@email.com", null),
                Arguments.of(null, null),
                Arguments.of("invalidEmail.com", "validPass123"),
                Arguments.of("valid@Emailcom", "invalidPass"),
                Arguments.of("12345@email.com", "invalidPass"),
                Arguments.of("1hello@email.com", "invalidPass"),
                Arguments.of("valid@email.com", "invalid_pass"),
                Arguments.of("valid@email.com", "invalidd"),
                Arguments.of("valid@email.com", "invald"),
                Arguments.of("valid@email.com", "in4a2d"),
                Arguments.of("valid@email.com", "iN4a2D"),
                Arguments.of("valid@email.com", "iN4a2Df"),
                Arguments.of("valid@email.com", "213455632345235"),
                Arguments.of("valid@email.com", "erjgwegojrogewgwej"),
                Arguments.of("valid@email.com", "REFOERJGNERJGOPERPGNO"),
                Arguments.of("valid@email.com", "freerFEWOWFFNoewnoewFOWEN")
        );
    }

    public static Stream<Arguments> invalidDataForSignUp() {
        return Stream.of(
                Arguments.of(CreateUserDto.builder().build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").firstName("john").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").firstName("john").lastName("lastname").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").firstName("john").lastName("").phone("375251002030").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").firstName("").lastName("lastname").phone("375251002030").build()),
                Arguments.of(CreateUserDto.builder().password("f34ef").email("valid@email.com").firstName("john").lastName("lastname").phone("375251002030").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("invalid@.com").firstName("john").lastName("lastname").phone("375251002030").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").firstName("john").lastName("lastname").phone("37525030").build()),
                Arguments.of(CreateUserDto.builder().email("valid@email.com").firstName("john").lastName("lastname").phone("375251002030").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").firstName("john").lastName("lastname").phone("375251002030").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").lastName("lastname").phone("375251002030").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").firstName("john").phone("375251002030").build()),
                Arguments.of(CreateUserDto.builder().password("asfasdf123").email("valid@email.com").firstName("john").lastName("lastname").build())
        );
    }

    public static Stream<Arguments> invalidUserDto() {
        return Stream.of(
                Arguments.of(UserDto.builder().build()),
                Arguments.of(UserDto.builder().id(-1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("375254444444").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("375254444444").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName(" ").lastName("Münz").phone("375254444444").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("      ").phone("375254444444").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("37544").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("375254444444").points(0L).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("375254444444").build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).build()),
                Arguments.of(UserDto.builder().email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("375254444444").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("375254444444").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).lastName("Münz").phone("375254444444").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").phone("375254444444").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").points(0L).isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("375254444444").isBlocked(false).build()),
                Arguments.of(UserDto.builder().id(1_000_003L).email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("375254444444").points(0L).build())
        );
    }

    public static Stream<Arguments> invalidPasswords() {
        return Stream.of(
                Arguments.of(null, "ferfrf3243"),
                Arguments.of(null, null),
                Arguments.of("ferfrf3243", null),
                Arguments.of("ferfrf3243", "ferfrfFREOFrejfer"),
                Arguments.of("ferfrfFREOFrejfer", "ferfrf3243"),
                Arguments.of("3432453453", "ferfrf3243"),
                Arguments.of("ferfrf3243", "3432453453")
        );
    }

    public static Stream<Arguments> invalidUserDtoForDeleteMethod() {
        return Stream.of(
                Arguments.of(UserDto.builder().build()),
                Arguments.of(UserDto.builder().id(-4L).build()),
                Arguments.of(UserDto.builder().id(0L).build()),
                Arguments.of(UserDto.builder().email("hans@gmail.com").role(UserRole.CLIENT).firstName("Hans").lastName("Münz").phone("+375254444444").points(0L).isBlocked(false).build())
        );
    }

    @BeforeEach
    void setUp() {
        doReturn(userDao).when(transaction).createDao(UserDao.class);
        doReturn(transaction).when(transactionFactory).createTransaction();
    }

    @Test
    @Tag("findAll")
    void shouldMapAllEntitiesToUserDto() throws DaoException, ServiceException {
        doReturn(List.of(IVAN, PETR, JOHN))
                .when(userDao).findAll();
        List<UserDto> expected = List.of(IVAN_DTO, PETR_DTO, JOHN_DTO);

        List<UserDto> actual = service.findAll();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("findAll")
    void shouldReturnEmptyListIfNoEntitiesInDb() throws DaoException, ServiceException {
        doReturn(Collections.emptyList())
                .when(userDao)
                .findAll();
        List<UserDto> expected = Collections.emptyList();

        List<UserDto> actual = service.findAll();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Tag("findAll")
    void shouldThrowExceptionIfDatabaseIsDisconnected() throws DaoException {
        doThrow(DaoException.class)
                .when(userDao)
                .findAll();

        assertThatThrownBy(() -> service.findAll()).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("findById")
    void shouldReturnUserDtoById() throws DaoException, ServiceException {
        doReturn(Optional.of(JOHN))
                .when(userDao)
                .findById(JOHN.getId());

        Optional<UserDto> actual = service.findById(JOHN.getId());

        assertThat(actual).isNotEmpty().contains(JOHN_DTO);
    }

    @Test
    @Tag("findById")
    void shouldReturnEmptyOptionalIfNoSuchId() throws DaoException, ServiceException {
        doReturn(Optional.empty())
                .when(userDao)
                .findById(78910L);

        Optional<UserDto> actual = service.findById(78910L);

        assertThat(actual).isEmpty();
    }

    @Test
    @Tag("findById")
    void shouldThrowExceptionIfIdIsInvalid() {
        assertThatThrownBy(() -> service.findById(-1L)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("signIn")
    void shouldReturnNotEmptyOptionalIfEmailWithGivenPasswordExists() throws DaoException, ServiceException {
        String rawPassword = "qwerty123";
        doReturn(Optional.of(PETR))
                .when(userDao)
                .findByEmail(PETR.getEmail());
        doReturn(true)
                .when(encoderService)
                .matches(rawPassword, PETR.getPassword());

        Optional<UserDto> actual = service.signIn(PETR.getEmail(), rawPassword);

        assertThat(actual).isNotEmpty().contains(PETR_DTO);
    }

    @Test
    @Tag("signIn")
    void shouldReturnEmptyOptionalIfPasswordDoesntMatch() throws DaoException, ServiceException {
        String invalidPass = "qwerty321";
        doReturn(Optional.of(PETR))
                .when(userDao)
                .findByEmail(PETR.getEmail());
        doReturn(false)
                .when(encoderService)
                .matches(invalidPass, PETR.getPassword());

        Optional<UserDto> actual = service.signIn(PETR.getEmail(), invalidPass);

        assertThat(actual).isEmpty();
    }

    @Test
    @Tag("signIn")
    void shouldReturnEmptyOptionalIfNoSuchEmail() throws DaoException, ServiceException {
        String dummy = "dummyPass123";
        doReturn(Optional.empty())
                .when(userDao)
                .findByEmail(hans.getEmail());

        Optional<UserDto> actual = service.signIn(hans.getEmail(), dummy);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidDataForSignIn")
    @Tag("signIn")
    void shouldThrowExceptionIfInvalidDataReceived(String email, String password) {
        assertThatThrownBy(() -> service.signIn(email, password))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("signUp")
    void shouldRegisterNewUserIfValidDataReceived() throws DaoException, ServiceException {
        doAnswer(
                invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1_000_003L);
                    return null;
                }).when(userDao)
                .create(hans);
        doReturn("$2a$10$.Hans.PASS.43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr2")
                .when(encoderService)
                .encode(hansCreateDto.getPassword());

        UserDto actual = service.signUp(hansCreateDto);

        assertThat(actual).isEqualTo(hansDto);
    }

    @ParameterizedTest
    @MethodSource("invalidDataForSignUp")
    @NullSource
    @Tag("signUp")
    void shouldThrowExceptionIfInvalidDataReceived(CreateUserDto dto) {
        assertThatThrownBy(() -> service.signUp(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("update")
    void shouldReturnTrueIfValidDataReceived() throws ServiceException, DaoException {
        adamDto.setLastName("LastName");

        adam.setLastName("Lastname");
        doReturn(true)
                .when(userDao)
                .update(adam);

        boolean actual = service.update(adamDto);

        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidUserDto")
    @NullSource
    @Tag("update")
    void shouldThrowExceptionIfInvalidDataReceivedForUpdateMethod(UserDto dto) {
        assertThatThrownBy(() -> service.update(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("updatePassword")
    void shouldReturnTrueIfValidDataReceivedForUpdatePasswordMethod() throws ServiceException, DaoException {
        String oldPassword = "qwerty123";
        String newPassword = "123qwerty";
        String hash = "$2a$10$.NEW.IVAN.f43NRAZ3Fny.C/6PveEH6JGzGo9X2SQSwM5djXwpdr2";
        doReturn(Optional.of(IVAN))
                .when(userDao)
                .findById(IVAN_DTO.getId());
        doReturn(true)
                .when(encoderService)
                .matches(oldPassword, IVAN.getPassword());
        doReturn(true)
                .when(userDao)
                .updatePassword(IVAN.getId(), hash);
        doReturn(hash)
                .when(encoderService)
                .encode(newPassword);

        boolean actual = service.updatePassword(IVAN_DTO, oldPassword, newPassword);

        assertThat(actual).isTrue();
    }

    @Test
    @Tag("updatePassword")
    void shouldReturnFalseIfNoSuchUserIdForUpdatePasswordMethod() throws ServiceException, DaoException {
        String oldPassword = "qwerty123";
        String newPassword = "123qwerty";
        doReturn(Optional.empty())
                .when(userDao)
                .findById(hansDto.getId());

        boolean actual = service.updatePassword(hansDto, oldPassword, newPassword);

        assertThat(actual).isFalse();
    }

    @Test
    @Tag("updatePassword")
    void shouldReturnFalseIfPasswordDoesntMatchForUpdatePasswordMethod() throws ServiceException, DaoException {
        String oldPassword = "invalidOld123Password";
        String newPassword = "123qwerty";
        doReturn(Optional.of(hans))
                .when(userDao)
                .findById(hansDto.getId());
        doReturn(false)
                .when(encoderService)
                .matches(any(), eq(hans.getPassword()));

        boolean actual = service.updatePassword(hansDto, oldPassword, newPassword);

        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("invalidUserDto")
    @NullSource
    @Tag("updatePassword")
    void shouldThrowExceptionIfInvalidDtoReceivedForUpdatePasswordMethod(UserDto dto) {
        String dummy = "dummyPass123";
        assertThatThrownBy(() -> service.updatePassword(dto, dummy, dummy)).isInstanceOf(ServiceException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    @Tag("updatePassword")
    void shouldThrowExceptionIfInvalidPasswordReceivedForUpdatePasswordMethod(String oldPassword, String newPassword) {
        UserDto dummy = adamDto;
        assertThatThrownBy(() -> service.updatePassword(dummy, oldPassword, newPassword)).isInstanceOf(ServiceException.class);
    }

    @Test
    @Tag("delete")
    void shouldReturnTrueIfValidDataReceivedForDeleteMethod() throws DaoException, ServiceException {
        doReturn(true)
                .when(userDao)
                .delete(PETR.getId());

        boolean actual = service.delete(PETR_DTO);

        assertThat(actual).isTrue();
    }

    @Test
    @Tag("delete")
    void shouldReturnFalseIfNoSuchUserIdForDeleteMethod() throws DaoException, ServiceException {
        hansDto.setId(894452L);
        doReturn(false)
                .when(userDao)
                .delete(hansDto.getId());

        boolean actual = service.delete(hansDto);

        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("invalidUserDtoForDeleteMethod")
    @NullSource
    @Tag("delete")
    void shouldThrowExceptionIfInvalidDataReceivedForDeleteMethod(UserDto dto) {
        assertThatThrownBy(() -> service.delete(dto)).isInstanceOf(ServiceException.class);
    }
}
