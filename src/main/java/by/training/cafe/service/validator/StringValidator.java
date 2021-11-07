package by.training.cafe.service.validator;

/**
 * The class {@code StringValidator} is a class
 * that implements {@link Validator}.<br/>
 * Checks if {@code string} is not null and not blank.
 *
 * @author Nikita Romanov
 */
public final class StringValidator implements Validator<String> {

    private static final StringValidator INSTANCE = new StringValidator();

    private StringValidator() {
    }

    public static StringValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(String object) {
        return object != null
                && !object.isBlank();
    }
}
