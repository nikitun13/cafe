package by.training.cafe.service.validator;

import by.training.cafe.dto.CreateUserDto;

/**
 * The class {@code CreateUserDtoValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates all fields of {@link CreateUserDto}.
 *
 * @author Nikita Romanov
 * @see CreateUserDto
 */
public final class CreateUserDtoValidator implements Validator<CreateUserDto> {

    private static final CreateUserDtoValidator INSTANCE
            = new CreateUserDtoValidator();
    private static final int NAME_MAX_LENGTH = 128;

    private final Validator<String> stringValidator
            = StringValidator.getInstance();
    private final Validator<String> emailValidator
            = EmailValidator.getInstance();
    private final Validator<String> phoneValidator
            = PhoneValidator.getInstance();
    private final Validator<String> passwordValidator
            = PasswordValidator.getInstance();

    private CreateUserDtoValidator() {
    }

    public static CreateUserDtoValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(CreateUserDto createUserDto) {
        if (createUserDto == null) {
            return false;
        }
        var email = createUserDto.getEmail();
        var password = createUserDto.getPassword();
        var repeatPassword = createUserDto.getRepeatPassword();
        var firstName = createUserDto.getFirstName();
        var lastName = createUserDto.getLastName();
        var phone = createUserDto.getPhone();

        return password != null
                && password.equals(repeatPassword)
                && emailValidator.isValid(email)
                && passwordValidator.isValid(password)
                && stringValidator.isValid(firstName)
                && firstName.length() <= NAME_MAX_LENGTH
                && stringValidator.isValid(lastName)
                && lastName.length() <= NAME_MAX_LENGTH
                && phoneValidator.isValid(phone);
    }
}
