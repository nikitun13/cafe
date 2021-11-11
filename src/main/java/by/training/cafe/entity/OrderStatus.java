package by.training.cafe.entity;

/**
 * The class {@code OrderStatus} is an enumeration
 * of the supported order statuses.
 *
 * @author Nikita Romanov
 */
public enum OrderStatus {

    CANCELED, COMPLETED, NOT_COLLECTED, PENDING;

    public static boolean contains(String test) {
        for (OrderStatus status : values()) {
            if (status.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
