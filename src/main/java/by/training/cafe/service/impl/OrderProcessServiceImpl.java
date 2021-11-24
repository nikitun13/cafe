package by.training.cafe.service.impl;

import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.OrderedDishDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.service.OrderProcessService;
import by.training.cafe.service.OrderService;
import by.training.cafe.service.OrderedDishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class {@code OrderProcessServiceImpl} is a class
 * that implements {@link OrderProcessService}.
 *
 * @author Nikita Romanov
 * @see OrderedDishService
 */
public class OrderProcessServiceImpl implements OrderProcessService {

    private static final Logger log
            = LogManager.getLogger(OrderProcessServiceImpl.class);
    private static final String INVALID_PARAMS_FORMAT_MESSAGE
            = "Retrieved params are invalid. "
            + "CreateOrderDto: %s. OrderedDishes: %s";
    private static final Duration FIVE_MINUTES
            = Duration.of(5L, ChronoUnit.MINUTES);
    private static final String PENDING = "Pending";

    private final OrderService orderService;
    private final OrderedDishService orderedDishService;
    private final UserService userService;

    public OrderProcessServiceImpl(OrderService orderService,
                                   OrderedDishService orderedDishService,
                                   UserService userService) {
        this.orderService = orderService;
        this.orderedDishService = orderedDishService;
        this.userService = userService;
    }

    @Override
    public void createOrder(CreateOrderDto createOrderDto,
                            List<OrderedDishDto> orderedDishes)
            throws ServiceException {
        log.debug("Received params CreateOrderDto: {}, orderedDishes: {}",
                createOrderDto, orderedDishes);
        if (!validateCreateParams(createOrderDto, orderedDishes)) {
            throw new ServiceException(INVALID_PARAMS_FORMAT_MESSAGE
                    .formatted(createOrderDto, orderedDishes));
        }
        orderedDishes.forEach(orderedDish -> orderedDish.setTotalPrice(
                orderedDish.getDishPrice() * orderedDish.getDishCount()
        ));
        long totalCartPrice = orderedDishes.stream()
                .mapToLong(OrderedDishDto::getTotalPrice)
                .sum();
        Long debitedPoints = createOrderDto.getDebitedPoints();
        long totalPrice = totalCartPrice - debitedPoints;
        createOrderDto.setTotalPrice(totalPrice);
        OrderDto orderDto = orderService.create(createOrderDto);

        try {
            for (OrderedDishDto orderedDish : orderedDishes) {
                orderedDish.setOrder(orderDto);
                orderedDishService.create(orderedDish);
            }
        } catch (ServiceException e) {
            log.debug("Exception occurred during adding OrderedDishes");
            orderService.delete(orderDto);
            log.debug("Empty order was deleted");
            throw e;
        }

        UserDto user = createOrderDto.getUser();
        Long points = user.getPoints() - debitedPoints;
        user.setPoints(points);

        userService.update(user);
    }

    @Override
    public void deleteNewOrder(OrderDto orderDto) throws ServiceException {
        if (!isDeletableOrder(orderDto, FIVE_MINUTES)) {
            throw new ServiceException(
                    "Order can't be deleted. OrderDto: " + orderDto);
        }
        orderService.delete(orderDto);
        UserDto user = orderDto.getUser();
        Long debitedPoints = orderDto.getDebitedPoints();
        Long points = user.getPoints() + debitedPoints;
        user.setPoints(points);

        userService.update(user);
    }

    @Override
    public boolean isDeletableOrder(OrderDto orderDto,
                                    Duration timeout) throws ServiceException {
        log.debug("Received orderDto: {}", orderDto);
        if (orderDto == null
                || orderDto.getCreatedAt() == null
                || timeout == null
                || orderDto.getStatus() == null) {
            throw new ServiceException(
                    "OrderDto or timeout is invalid. Order dto: " + orderDto);
        }
        log.debug("timeout: {}", timeout.toSeconds());
        if (!orderDto.getStatus().equals(PENDING)) {
            log.debug("OrderDto is not pending. OrderDto: {}", orderDto);
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        log.debug("Now {}", now);
        LocalDateTime createdAt = orderDto.getCreatedAt().toLocalDateTime();
        log.debug("Created at: {}", createdAt);
        Duration between = Duration.between(createdAt, now);
        log.debug("Duration between: {} seconds", between.toSeconds());
        boolean isDeletable = timeout.compareTo(between) >= 0;
        log.debug("Order {} isDeletable: {}", orderDto, isDeletable);
        return isDeletable;
    }

    @Override
    public Map<OrderDto, Boolean> isDeletableOrders(List<OrderDto> orders,
                                                    Duration timeout)
            throws ServiceException {
        log.debug("Received orders: {}", orders);
        if (orders == null) {
            throw new ServiceException("Orders can't be null");
        }
        Map<OrderDto, Boolean> result = new HashMap<>();
        for (OrderDto order : orders) {
            result.put(order, isDeletableOrder(order, timeout));
        }
        log.debug("Result map: {}", result);
        return result;
    }

    private boolean validateCreateParams(CreateOrderDto createOrderDto,
                                         List<OrderedDishDto> orderedDishes) {
        if (createOrderDto == null || orderedDishes == null
                || createOrderDto.getDebitedPoints() == null) {
            return false;
        }
        for (OrderedDishDto orderedDish : orderedDishes) {
            if (orderedDish.getDishPrice() == null
                    || orderedDish.getDishCount() == null) {
                return false;
            }
        }
        return true;
    }
}
