package by.training.cafe.service.mapper;

import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.OrderedDishDto;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderedDish;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@code DishMapper} is a class
 * that implements {@link Mapper}.<br/>
 * Maps {@link OrderedDish} to {@link OrderedDishDto} and vice versa.
 *
 * @author Nikita Romanov
 * @see Mapper
 * @see OrderedDish
 * @see OrderedDishDto
 */
public final class OrderedDishMapper
        implements Mapper<OrderedDish, OrderedDishDto> {

    private static final Logger log
            = LogManager.getLogger(OrderedDishMapper.class);
    private static final OrderedDishMapper INSTANCE
            = new OrderedDishMapper();

    private final Mapper<Order, OrderDto> orderDtoMapper
            = OrderDtoMapper.getInstance();
    private final Mapper<Dish, DishDto> dishDishMapper
            = DishMapper.getInstance();

    private OrderedDishMapper() {
    }

    public static OrderedDishMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public OrderedDish mapDtoToEntity(OrderedDishDto dto) {
        log.debug("received dto: {}", dto);
        var order = orderDtoMapper.mapDtoToEntity(dto.getOrder());
        var dish = dishDishMapper.mapDtoToEntity(dto.getDish());
        var dishPrice = dto.getDishPrice();
        var dishCount = dto.getDishCount();

        OrderedDish orderedDish = OrderedDish.builder()
                .order(order)
                .dish(dish)
                .dishPrice(dishPrice)
                .dishCount(dishCount)
                .build();
        log.debug("result OrderedDish: {}", orderedDish);
        return orderedDish;
    }

    @Override
    public OrderedDishDto mapEntityToDto(OrderedDish entity) {
        log.debug("received OrderedDish: {}", entity);
        var orderDto = orderDtoMapper.mapEntityToDto(entity.getOrder());
        var dishDto = dishDishMapper.mapEntityToDto(entity.getDish());
        var dishPrice = entity.getDishPrice();
        var dishCount = entity.getDishCount();
        var totalPrice = entity.getTotalPrice();

        OrderedDishDto createDishDto = OrderedDishDto.builder()
                .order(orderDto)
                .dish(dishDto)
                .dishPrice(dishPrice)
                .dishCount(dishCount)
                .totalPrice(totalPrice)
                .build();
        log.debug("result OrderedDishDto: {}", createDishDto);
        return createDishDto;
    }
}
