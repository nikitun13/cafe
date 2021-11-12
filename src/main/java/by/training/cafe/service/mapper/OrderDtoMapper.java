package by.training.cafe.service.mapper;

import by.training.cafe.dto.OrderDto;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderStatus;
import by.training.cafe.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@code OrderDtoMapper} is a class
 * that implements {@link Mapper}.<br/>
 * Maps {@link Order} to {@link OrderDto} and vice versa.
 *
 * @author Nikita Romanov
 * @see Mapper
 * @see Order
 * @see OrderDto
 */
public final class OrderDtoMapper implements Mapper<Order, OrderDto> {

    private static final Logger log
            = LogManager.getLogger(OrderDtoMapper.class);

    private static final OrderDtoMapper INSTANCE = new OrderDtoMapper();

    private final UserDtoMapper userDtoMapper = UserDtoMapper.getInstance();

    private OrderDtoMapper() {
    }

    public static OrderDtoMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Order mapDtoToEntity(OrderDto dto) {
        log.debug("Received OrderDto: {}", dto);
        var id = dto.getId();
        var user = userDtoMapper.mapDtoToEntity(dto.getUser());
        var createdAt = dto.getCreatedAt();
        var expectedRetrieveDate = dto.getExpectedRetrieveDate();
        var actualRetrieveDate = dto.getActualRetrieveDate();
        var status = OrderStatus.valueOf(dto.getStatus().toUpperCase());
        var debitedPoints = dto.getDebitedPoints();
        var accruedPoints = dto.getAccruedPoints();
        var totalPrice = dto.getTotalPrice();


        Order order = Order.builder()
                .id(id)
                .user(user)
                .createdAt(createdAt)
                .expectedRetrieveDate(expectedRetrieveDate)
                .actualRetrieveDate(actualRetrieveDate)
                .status(status)
                .debitedPoints(debitedPoints)
                .accruedPoints(accruedPoints)
                .totalPrice(totalPrice)
                .build();
        log.debug("Result order: {}", order);
        return order;
    }

    @Override
    public OrderDto mapEntityToDto(Order order) {
        log.debug("Received order: {}", order);
        var id = order.getId();
        var user = userDtoMapper.mapEntityToDto(order.getUser());
        var createdAt = order.getCreatedAt();
        var expectedRetrieveDate = order.getExpectedRetrieveDate();
        var actualRetrieveDate = order.getActualRetrieveDate();
        var status = StringUtil.capitalizeFirstLetter(
                order.getStatus().name().toLowerCase());
        var debitedPoints = order.getDebitedPoints();
        var accruedPoints = order.getAccruedPoints();
        var totalPrice = order.getTotalPrice();


        OrderDto orderDto = OrderDto.builder()
                .id(id)
                .user(user)
                .createdAt(createdAt)
                .expectedRetrieveDate(expectedRetrieveDate)
                .actualRetrieveDate(actualRetrieveDate)
                .status(status)
                .debitedPoints(debitedPoints)
                .accruedPoints(accruedPoints)
                .totalPrice(totalPrice)
                .build();
        log.debug("Result orderDto: {}", orderDto);
        return orderDto;
    }
}
