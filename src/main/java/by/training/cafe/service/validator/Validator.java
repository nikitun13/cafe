package by.training.cafe.service.validator;

/**
 * The class {@code Validator} is an interface
 * that validates objects.
 *
 * @param <T> type of the object to be validated.
 * @author Nikita Romanov
 */
public interface Validator<T> {

    /**
     * Validates {@code object}.
     *
     * @param object {@code object} to be validated.
     * @return {@code true} if {@code object} is valid,
     * {@code false} otherwise.
     */
    boolean isValid(T object);
}
