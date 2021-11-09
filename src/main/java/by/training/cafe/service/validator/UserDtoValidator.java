package by.training.cafe.service.validator;

import by.training.cafe.dto.UserDto;

/**
 * The class {@code UserDtoValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates all fields of {@link UserDto}.
 *
 * @author Nikita Romanov
 * @see UserDto
 */
public final class UserDtoValidator implements Validator<UserDto> {

    private static final UserDtoValidator INSTANCE = new UserDtoValidator();

    private final Validator<String> stringValidator
            = StringValidator.getInstance();
    private final Validator<String> emailValidator
            = EmailValidator.getInstance();
    private final Validator<String> phoneValidator
            = PhoneValidator.getInstance();

    private UserDtoValidator() {
    }

    public static UserDtoValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(UserDto userDto) {
        if (userDto == null) {
            return false;
        }
        var id = userDto.getId();
        var email = userDto.getEmail();
        var role = userDto.getRole();
        var firstName = userDto.getFirstName();
        var lastName = userDto.getLastName();
        var phone = userDto.getPhone();
        var points = userDto.getPoints();
        var isBlocked = userDto.isBlocked();

        return id != null
                && id > 0
                && emailValidator.isValid(email)
                && role != null
                && stringValidator.isValid(firstName)
                && stringValidator.isValid(lastName)
                && phoneValidator.isValid(phone)
                && points != null
                && isBlocked != null;
    }
}
