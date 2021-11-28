package by.training.cafe.service.mapper;

import by.training.cafe.dto.DishDto;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.DishCategory;
import by.training.cafe.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@code DishMapper} is a class
 * that implements {@link Mapper}.<br/>
 * Maps {@code Dish} to {@code DishDto} and vice versa.
 *
 * @author Nikita Romanov
 * @see Mapper
 */
public final class DishMapper implements Mapper<Dish, DishDto> {

    private static final Logger log = LogManager.getLogger(DishMapper.class);
    private static final DishMapper INSTANCE = new DishMapper();

    private DishMapper() {
    }

    public static DishMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Dish mapDtoToEntity(DishDto dto) {
        log.debug("received dto: {}", dto);
        var id = dto.getId();
        var name = dto.getName()
                .strip()
                .replaceAll("\\s{2,}", " ");
        var category = DishCategory.valueOf(
                dto.getCategory().toUpperCase());
        var price = dto.getPrice();
        var description = dto.getDescription()
                .strip()
                .replaceAll("\\s{2,}", " ");

        Dish dish = Dish.builder()
                .id(id)
                .name(name)
                .category(category)
                .price(price)
                .description(description)
                .build();
        log.debug("result dish: {}", dish);
        return dish;
    }

    @Override
    public DishDto mapEntityToDto(Dish entity) {
        log.debug("received dish: {}", entity);
        var id = entity.getId();
        var name = entity.getName();
        var category = StringUtil.capitalizeFirstLetter(
                entity.getCategory().name().toLowerCase());
        var price = entity.getPrice();
        var description = entity.getDescription();

        DishDto dishDto = DishDto.builder()
                .id(id)
                .name(name)
                .category(category)
                .price(price)
                .description(description)
                .build();
        log.debug("result dishDto: {}", dishDto);
        return dishDto;
    }
}
