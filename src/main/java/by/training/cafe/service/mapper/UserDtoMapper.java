package by.training.cafe.service.mapper;

import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.User;
import by.training.cafe.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@code UserDtoMapper} is a class
 * that implements {@link Mapper}.<br/>
 * Maps {@link User} to {@link UserDto} and vice versa.
 *
 * @author Nikita Romanov
 * @see Mapper
 * @see User
 * @see UserDto
 */
public final class UserDtoMapper implements Mapper<User, UserDto> {

    private static final Logger log = LogManager.getLogger(UserDtoMapper.class);
    private static final UserDtoMapper INSTANCE = new UserDtoMapper();

    private UserDtoMapper() {
    }

    public static UserDtoMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public User mapDtoToEntity(UserDto userDto) {
        log.debug("received UserDto: {}", userDto);
        var id = userDto.getId();
        var email = userDto.getEmail();
        var role = userDto.getRole();
        var firstName = StringUtil.capitalizeFirstLetter(
                userDto.getFirstName()
                        .strip()
                        .replaceAll("\\s{2,}", " ")
                        .toLowerCase());
        var lastName = StringUtil.capitalizeFirstLetter(
                userDto.getLastName()
                        .strip()
                        .replaceAll("\\s{2,}", " ")
                        .toLowerCase());
        var phone = userDto.getPhone();
        var points = userDto.getPoints();
        var isBlocked = userDto.isBlocked();

        User user = User.builder()
                .id(id)
                .email(email)
                .role(role)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .points(points)
                .isBlocked(isBlocked)
                .build();

        log.debug("result User: {}", user);
        return user;
    }

    @Override
    public UserDto mapEntityToDto(User user) {
        log.debug("received User: {}", user);
        var id = user.getId();
        var email = user.getEmail();
        var role = user.getRole();
        var firstName = user.getFirstName();
        var lastName = user.getLastName();
        var phone = user.getPhone();
        var points = user.getPoints();
        var isBlocked = user.isBlocked();

        UserDto userDto = UserDto.builder()
                .id(id)
                .email(email)
                .role(role)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .points(points)
                .isBlocked(isBlocked)
                .build();

        log.debug("result UserDto: {}", userDto);
        return userDto;
    }
}
