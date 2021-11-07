package by.training.cafe.service.validator;

import by.training.cafe.dto.DishDto;
import by.training.cafe.entity.DishCategory;

/**
 * The class {@code DishDtoValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates all fields of {@link DishDto} without {@code id}.
 *
 * @author Nikita Romanov
 * @see DishDto
 */
public final class DishDtoValidator implements Validator<DishDto> {

    private static final DishDtoValidator INSTANCE = new DishDtoValidator();

    private final Validator<String> stringValidator
            = StringValidator.getInstance();

    private DishDtoValidator() {
    }

    public static DishDtoValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(DishDto object) {
        if (object == null) {
            return false;
        }
        String name = object.getName();
        String category = object.getCategory();
        Long price = object.getPrice();
        String description = object.getDescription();

        return stringValidator.isValid(name)
                && stringValidator.isValid(category)
                && DishCategory.contains(category.toUpperCase())
                && price != null
                && price > 0L
                && stringValidator.isValid(description);
    }
}
