package by.training.cafe.service.validator;

/**
 * The class {@code PhoneValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates phone using Regular Expression.<br/>
 * Phone must have from 10 to 15 digits.
 *
 * @author Nikita Romanov
 */
public final class PhoneValidator implements Validator<String> {

    private static final PhoneValidator INSTANCE = new PhoneValidator();
    private static final String PHONE_REGEX = "^\\d{10,15}$";

    private final Validator<String> stringValidator
            = StringValidator.getInstance();

    private PhoneValidator() {
    }

    public static PhoneValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(String phone) {
        return stringValidator.isValid(phone)
                && phone.matches(PHONE_REGEX);
    }
}
