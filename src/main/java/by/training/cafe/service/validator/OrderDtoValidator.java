package by.training.cafe.service.validator;

import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.OrderStatus;

/**
 * The class {@code OrderDtoValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates all fields of {@link OrderDto}.
 *
 * @author Nikita Romanov
 * @see OrderDto
 */
public final class OrderDtoValidator implements Validator<OrderDto> {

    private static final OrderDtoValidator INSTANCE
            = new OrderDtoValidator();

    private final Validator<UserDto> userDtoValidator
            = UserDtoValidator.getInstance();
    private final Validator<String> stringValidator
            = StringValidator.getInstance();

    private OrderDtoValidator() {
    }

    public static OrderDtoValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(OrderDto dto) {
        if (dto == null) {
            return false;
        }
        var id = dto.getId();
        var user = dto.getUser();
        var createdAt = dto.getCreatedAt();
        var expectedRetrieveDate = dto.getExpectedRetrieveDate();
        var status = dto.getStatus();
        var debitedPoints = dto.getDebitedPoints();
        var accruedPoints = dto.getAccruedPoints();
        var totalPrice = dto.getTotalPrice();
        var actualRetrieveDate = dto.getActualRetrieveDate();

        return id != null
                && id > 0
                && userDtoValidator.isValid(user)
                && createdAt != null
                && expectedRetrieveDate != null
                && createdAt.before(expectedRetrieveDate)
                && stringValidator.isValid(status)
                && OrderStatus.contains(status.toUpperCase())
                && debitedPoints != null
                && debitedPoints >= 0
                && accruedPoints != null
                && accruedPoints >= 0
                && totalPrice != null
                && totalPrice >= 0
                && (actualRetrieveDate == null
                || createdAt.before(actualRetrieveDate));
    }
}
