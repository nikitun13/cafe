package by.training.cafe.service.validator;

/**
 * The class {@code PasswordValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates password using Regular Expression.<br/>
 * Password must be minimum eight characters,
 * at least one letter and one number.
 *
 * @author Nikita Romanov
 */
public final class PasswordValidator implements Validator<String> {

    private static final PasswordValidator INSTANCE = new PasswordValidator();
    private static final String PASSWORD_REGEX
            = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}";
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final Validator<String> stringValidator
            = StringValidator.getInstance();

    private PasswordValidator() {
    }

    public static PasswordValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(String password) {
        return stringValidator.isValid(password)
                && password.length() <= MAX_PASSWORD_LENGTH
                && password.length() >= MIN_PASSWORD_LENGTH
                && password.matches(PASSWORD_REGEX);
    }
}
