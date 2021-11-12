package by.training.cafe.service.mapper;

import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@code CreateOrderDtoMapper} is a class
 * that implements {@link Mapper}.<br/>
 * Maps {@link Order} to {@link CreateOrderDto} and vice versa.
 *
 * @author Nikita Romanov
 * @see Mapper
 * @see Order
 * @see CreateOrderDto
 */
public final class CreateOrderDtoMapper implements Mapper<Order, CreateOrderDto> {

    private static final Logger log
            = LogManager.getLogger(CreateOrderDtoMapper.class);
    private static final CreateOrderDtoMapper INSTANCE
            = new CreateOrderDtoMapper();

    private final UserDtoMapper userDtoMapper = UserDtoMapper.getInstance();

    private CreateOrderDtoMapper() {
    }

    public static CreateOrderDtoMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Order mapDtoToEntity(CreateOrderDto dto) {
        log.debug("Received CreateOrderDto: {}", dto);
        var user = userDtoMapper.mapDtoToEntity(dto.getUser());
        var createdAt = dto.getCreatedAt();
        var expectedRetrieveDate = dto.getExpectedRetrieveDate();
        var debitedPoints = dto.getDebitedPoints();
        var totalPrice = dto.getTotalPrice();

        var status = OrderStatus.PENDING;
        var accruedPoints = 0L;

        Order order = Order.builder()
                .user(user)
                .createdAt(createdAt)
                .expectedRetrieveDate(expectedRetrieveDate)
                .status(status)
                .debitedPoints(debitedPoints)
                .accruedPoints(accruedPoints)
                .totalPrice(totalPrice)
                .build();
        log.debug("Result order: {}", order);
        return order;
    }

    @Override
    public CreateOrderDto mapEntityToDto(Order order) {
        log.debug("Received order: {}", order);
        var user = userDtoMapper.mapEntityToDto(order.getUser());
        var createdAt = order.getCreatedAt();
        var expectedRetrieveDate = order.getExpectedRetrieveDate();
        var debitedPoints = order.getDebitedPoints();
        var totalPrice = order.getTotalPrice();

        CreateOrderDto createOrderDto = CreateOrderDto.builder()
                .user(user)
                .createdAt(createdAt)
                .expectedRetrieveDate(expectedRetrieveDate)
                .debitedPoints(debitedPoints)
                .totalPrice(totalPrice)
                .build();
        log.debug("Result CreateOrderDto: {}", createOrderDto);
        return createOrderDto;
    }
}
