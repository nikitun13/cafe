package by.training.cafe.service.validator;

import by.training.cafe.dto.CommentDto;
import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.UserDto;

/**
 * The class {@code CommentDtoValidator} is a class
 * that implements {@link Validator}.<br/>
 * Validates all fields of {@link CommentDto} without {@code id}.
 *
 * @author Nikita Romanov
 * @see DishDto
 */
public final class CommentDtoValidator implements Validator<CommentDto> {

    private static final CommentDtoValidator INSTANCE = new CommentDtoValidator();
    private static final short MIN_RATING = 1;
    private static final short MAX_RATING = 5;
    private static final int MAX_BODY_LENGTH = 1024;

    private final Validator<String> stringValidator
            = StringValidator.getInstance();
    private final Validator<UserDto> userDtoValidator
            = UserDtoValidator.getInstance();
    private final Validator<DishDto> dishDtoValidator
            = DishDtoValidator.getInstance();

    private CommentDtoValidator() {
    }

    public static CommentDtoValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValid(CommentDto commentDto) {
        if (commentDto == null) {
            return false;
        }
        var user = commentDto.getUser();
        var dish = commentDto.getDish();
        var rating = commentDto.getRating();
        var body = commentDto.getBody();
        var createdAt = commentDto.getCreatedAt();

        return userDtoValidator.isValid(user)
                && dishDtoValidator.isValid(dish)
                && rating != null
                && rating >= MIN_RATING
                && rating <= MAX_RATING
                && stringValidator.isValid(body)
                && body.length() <= MAX_BODY_LENGTH
                && createdAt != null;
    }
}
