package by.training.cafe.service.mapper;

import by.training.cafe.dto.CreateUserDto;
import by.training.cafe.entity.User;
import by.training.cafe.entity.UserRole;
import by.training.cafe.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@code CreateUserDtoMapper} is a class
 * that implements {@link Mapper}.<br/>
 * Maps {@link User} to {@link CreateUserDto} and vice versa.
 *
 * @author Nikita Romanov
 * @see Mapper
 * @see User
 * @see CreateUserDto
 */
public final class CreateUserDtoMapper implements Mapper<User, CreateUserDto> {

    private static final Logger log
            = LogManager.getLogger(CreateUserDtoMapper.class);
    private static final CreateUserDtoMapper INSTANCE
            = new CreateUserDtoMapper();
    private static final UserRole DEFAULT_ROLE = UserRole.CLIENT;
    private static final Long DEFAULT_POINTS = 0L;
    private static final Boolean DEFAULT_IS_BLOCKED = Boolean.FALSE;

    private CreateUserDtoMapper() {
    }

    public static CreateUserDtoMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public User mapDtoToEntity(CreateUserDto createUserDto) {
        log.debug("received UserDto: {}", createUserDto);
        var email = createUserDto.getEmail();
        var password = createUserDto.getPassword();
        var firstName = StringUtil.capitalizeFirstLetter(
                createUserDto.getFirstName().toLowerCase());
        var lastName = StringUtil.capitalizeFirstLetter(
                createUserDto.getLastName().toLowerCase());
        var phone = createUserDto.getPhone();
        User user = User.builder()
                .email(email)
                .password(password)
                .role(DEFAULT_ROLE)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .points(DEFAULT_POINTS)
                .isBlocked(DEFAULT_IS_BLOCKED)
                .build();

        log.debug("result User: {}", user);
        return user;
    }

    @Override
    public CreateUserDto mapEntityToDto(User user) {
        log.debug("received User: {}", user);
        var email = user.getEmail();
        var password = user.getPassword();
        var firstName = user.getFirstName();
        var lastName = user.getLastName();
        var phone = user.getPhone();

        CreateUserDto createUserDto = CreateUserDto.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .build();

        log.debug("result CreateUserDto: {}", createUserDto);
        return createUserDto;
    }
}
