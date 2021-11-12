package by.training.cafe.service.validator;

import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.dto.UserDto;

/**
 * The class {@code CreateOrderDtoValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates all fields of {@link CreateOrderDto}.
 *
 * @author Nikita Romanov
 * @see CreateOrderDto
 */
public final class CreateOrderDtoValidator implements Validator<CreateOrderDto> {

    private static final CreateOrderDtoValidator INSTANCE
            = new CreateOrderDtoValidator();

    private final Validator<UserDto> userDtoValidator
            = UserDtoValidator.getInstance();

    private CreateOrderDtoValidator() {
    }

    public static CreateOrderDtoValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(CreateOrderDto dto) {
        if (dto == null) {
            return false;
        }
        var user = dto.getUser();
        var createdAt = dto.getCreatedAt();
        var expectedRetrieveDate = dto.getExpectedRetrieveDate();
        var debitedPoints = dto.getDebitedPoints();
        var totalPrice = dto.getTotalPrice();

        return userDtoValidator.isValid(user)
                && createdAt != null
                && expectedRetrieveDate != null
                && createdAt.before(expectedRetrieveDate)
                && debitedPoints != null
                && debitedPoints >= 0
                && totalPrice != null
                && totalPrice >= 0;
    }
}
