package by.training.cafe.service.validator;

/**
 * The class {@code EmailValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates email using Regular Expression. Max length
 * of full email is 254 characters.
 * The following restrictions are imposed in the email
 * addresses local-part by using this regex:
 * <ul>
 *      <li>It allows numeric values from 0 to 9</li>
 *      <li>Both uppercase and lowercase letters from a to z are allowed
 *      <li>Allowed are underscore “_”, hyphen “-” and dot “.”
 *      <li>Dot isn't allowed at the start and end of the local-part
 *      <li>Consecutive dots aren't allowed
 *      <li>For the local part, a maximum of 64 characters are allowed
 * </ul>
 * Restrictions for the domain-part in this regular expression include:
 * <ul>
 *      <li>It allows numeric values from 0 to 9</li>
 *      <li>We allow both uppercase and lowercase letters from a to z</li>
 *      <li>Hyphen “-” and dot “.” isn't allowed at the start and end of the domain-part</li>
 *      <li>No consecutive dots</li>
 * </ul>
 *
 * @author Nikita Romanov
 */
public final class EmailValidator implements Validator<String> {

    private static final EmailValidator INSTANCE = new EmailValidator();
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final String EMAIL_REGEX
            = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-.][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    private final Validator<String> stringValidator
            = StringValidator.getInstance();

    private EmailValidator() {
    }

    public static EmailValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(String email) {
        return stringValidator.isValid(email)
                && email.length() <= MAX_EMAIL_LENGTH
                && email.matches(EMAIL_REGEX);
    }
}
