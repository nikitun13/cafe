package by.training.cafe.service.validator;

import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.OrderedDishDto;

/**
 * The class {@code OrderedDishDtoValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates all fields of {@link OrderedDishDto}.
 *
 * @author Nikita Romanov
 * @see OrderedDishDto
 */
public final class OrderedDishDtoValidator
        implements Validator<OrderedDishDto> {

    private static final OrderedDishDtoValidator INSTANCE
            = new OrderedDishDtoValidator();

    private final Validator<OrderDto> orderDtoValidator
            = OrderDtoValidator.getInstance();
    private final Validator<DishDto> dishDtoValidator
            = DishDtoValidator.getInstance();

    private OrderedDishDtoValidator() {
    }

    public static OrderedDishDtoValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(OrderedDishDto orderedDishDto) {
        if (orderedDishDto == null) {
            return false;
        }
        var orderDto = orderedDishDto.getOrder();
        var dishDto = orderedDishDto.getDish();
        var dishPrice = orderedDishDto.getDishPrice();
        var dishCount = orderedDishDto.getDishCount();

        return orderDtoValidator.isValid(orderDto)
                && dishDtoValidator.isValid(dishDto)
                && dishPrice != null
                && dishPrice >= 0L
                && dishCount != null
                && dishCount > 0;
    }
}
