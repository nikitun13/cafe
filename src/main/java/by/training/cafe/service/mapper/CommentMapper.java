package by.training.cafe.service.mapper;

import by.training.cafe.dto.CommentDto;
import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.Comment;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@code CommentMapper} is a class
 * that implements {@link Mapper}.<br/>
 * Maps {@link Comment} to {@link CommentDto} and vice versa.
 *
 * @author Nikita Romanov
 * @see Mapper
 */
public final class CommentMapper implements Mapper<Comment, CommentDto> {

    private static final Logger log = LogManager.getLogger(CommentMapper.class);
    private static final CommentMapper INSTANCE = new CommentMapper();

    private final Mapper<User, UserDto> userDtoMapper
            = UserDtoMapper.getInstance();
    private final Mapper<Dish, DishDto> dishMapper
            = DishMapper.getInstance();

    private CommentMapper() {
    }

    public static CommentMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Comment mapDtoToEntity(CommentDto dto) {
        log.debug("received CommentDto: {}", dto);
        var id = dto.getId();
        var user = userDtoMapper.mapDtoToEntity(dto.getUser());
        var dish = dishMapper.mapDtoToEntity(dto.getDish());
        var rating = dto.getRating();
        var body = dto.getBody();
        var createdAt = dto.getCreatedAt();

        Comment comment = Comment.builder()
                .id(id)
                .user(user)
                .dish(dish)
                .rating(rating)
                .body(body)
                .createdAt(createdAt)
                .build();
        log.debug("result comment: {}", comment);
        return comment;
    }

    @Override
    public CommentDto mapEntityToDto(Comment entity) {
        log.debug("received Comment: {}", entity);
        var id = entity.getId();
        var userDto = userDtoMapper.mapEntityToDto(entity.getUser());
        var dishDto = dishMapper.mapEntityToDto(entity.getDish());
        var rating = entity.getRating();
        var body = entity.getBody();
        var createdAt = entity.getCreatedAt();

        CommentDto commentDto = CommentDto.builder()
                .id(id)
                .user(userDto)
                .dish(dishDto)
                .rating(rating)
                .body(body)
                .createdAt(createdAt)
                .build();
        log.debug("result CommentDto: {}", commentDto);
        return commentDto;
    }
}
