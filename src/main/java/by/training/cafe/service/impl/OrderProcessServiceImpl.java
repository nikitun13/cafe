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

import java.sql.Timestamp;
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
    private static final String COMPLETED = "Completed";
    private static final String NOT_COLLECTED = "Not_collected";
    private static final String CANCELED = "Canceled";
    private static final double FIVE_PERCENT = 0.05;
    private static final String RECEIVED_ORDER_DTO_LOG_MESSAGE
            = "Received orderDto: {}";
    private static final String ORDER_DTO_IS_INVALID_MESSAGE
            = "OrderDto is invalid. Order dto: ";
    private static final String RECEIVED_ORDERS_LIST_LOG_MESSAGE
            = "Received orders list: {}";
    private static final String ORDERS_CANT_BE_NULL_MESSAGE
            = "Orders can't be null";
    private static final String NOT_ENOUGH_POINTS_MESSAGE
            = "Not enough points. Points: ";
    private static final int MAX_NOT_COLLECTED_ORDERS = 2;

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
        log.debug(RECEIVED_ORDER_DTO_LOG_MESSAGE, orderDto);
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
        log.debug(RECEIVED_ORDERS_LIST_LOG_MESSAGE, orders);
        if (orders == null) {
            throw new ServiceException(ORDERS_CANT_BE_NULL_MESSAGE);
        }
        Map<OrderDto, Boolean> result = new HashMap<>();
        for (OrderDto order : orders) {
            result.put(order, isDeletableOrder(order, timeout));
        }
        log.debug("Result map: {}", result);
        return result;
    }

    @Override
    public void completeOrder(OrderDto orderDto) throws ServiceException {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        completeOrder(orderDto, now);
    }

    @Override
    public void completeOrders(List<OrderDto> orders) throws ServiceException {
        log.debug(RECEIVED_ORDERS_LIST_LOG_MESSAGE, orders);
        if (orders == null) {
            throw new ServiceException(ORDERS_CANT_BE_NULL_MESSAGE);
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (OrderDto order : orders) {
            completeOrder(order, now);
        }
    }

    @Override
    public void cancelOrder(OrderDto orderDto) throws ServiceException {
        log.debug(RECEIVED_ORDER_DTO_LOG_MESSAGE, orderDto);
        if (!validateOrderDto(orderDto)) {
            throw new ServiceException(ORDER_DTO_IS_INVALID_MESSAGE + orderDto);
        }
        if (orderDto.getStatus().equals(CANCELED)) {
            return;
        }
        Long accruedPoints = orderDto.getAccruedPoints();
        Long debitedPoints = orderDto.getDebitedPoints();
        UserDto user = orderDto.getUser();
        Long currentPoints = user.getPoints();
        long points = currentPoints + debitedPoints - accruedPoints;
        if (points < 0L) {
            throw new ServiceException(NOT_ENOUGH_POINTS_MESSAGE + points);
        }

        Long currentTotalPrice = orderDto.getTotalPrice();
        Long totalPrice = currentTotalPrice + debitedPoints;
        orderDto.setTotalPrice(totalPrice);
        user.setPoints(points);
        orderDto.setStatus(CANCELED);
        orderDto.setAccruedPoints(0L);
        orderDto.setDebitedPoints(0L);
        orderDto.setActualRetrieveDate(null);

        orderService.update(orderDto);
        userService.update(user);
    }

    @Override
    public void cancelOrders(List<OrderDto> orders) throws ServiceException {
        log.debug(RECEIVED_ORDERS_LIST_LOG_MESSAGE, orders);
        if (orders == null) {
            throw new ServiceException(ORDERS_CANT_BE_NULL_MESSAGE);
        }
        for (OrderDto order : orders) {
            cancelOrder(order);
        }
    }

    @Override
    public void toPendingOrder(OrderDto orderDto) throws ServiceException {
        log.debug(RECEIVED_ORDER_DTO_LOG_MESSAGE, orderDto);
        if (!validateOrderDto(orderDto)) {
            throw new ServiceException(ORDER_DTO_IS_INVALID_MESSAGE + orderDto);
        }
        if (orderDto.getStatus().equals(PENDING)) {
            return;
        }
        Long accruedPoints = orderDto.getAccruedPoints();
        UserDto user = orderDto.getUser();
        Long currentPoints = user.getPoints();
        long points = currentPoints - accruedPoints;
        if (points < 0L) {
            throw new ServiceException(NOT_ENOUGH_POINTS_MESSAGE + points);
        }

        orderDto.setStatus(PENDING);
        orderDto.setAccruedPoints(0L);
        orderDto.setActualRetrieveDate(null);

        orderService.update(orderDto);

        user.setPoints(points);
        userService.update(user);
    }

    @Override
    public void toPendingOrders(List<OrderDto> orders) throws ServiceException {
        log.debug(RECEIVED_ORDERS_LIST_LOG_MESSAGE, orders);
        if (orders == null) {
            throw new ServiceException(ORDERS_CANT_BE_NULL_MESSAGE);
        }
        for (OrderDto order : orders) {
            toPendingOrder(order);
        }
    }

    @Override
    public void toNotCollectedOrder(OrderDto orderDto) throws ServiceException {
        log.debug(RECEIVED_ORDER_DTO_LOG_MESSAGE, orderDto);
        if (!validateOrderDto(orderDto)) {
            throw new ServiceException(ORDER_DTO_IS_INVALID_MESSAGE + orderDto);
        }
        if (orderDto.getStatus().equals(NOT_COLLECTED)) {
            return;
        }
        Long accruedPoints = orderDto.getAccruedPoints();
        UserDto user = orderDto.getUser();
        Long currentPoints = user.getPoints();
        long points = currentPoints - accruedPoints;
        if (points < 0L) {
            throw new ServiceException(NOT_ENOUGH_POINTS_MESSAGE + points);
        }
        orderDto.setStatus(NOT_COLLECTED);
        orderDto.setAccruedPoints(0L);
        orderDto.setActualRetrieveDate(null);

        orderService.update(orderDto);

        user.setPoints(points);
        Long notCollectedOrders
                = orderService.countNotCollectedOrdersByUserId(user.getId());
        if (notCollectedOrders > MAX_NOT_COLLECTED_ORDERS) {
            user.setBlocked(Boolean.TRUE);
        }
        userService.update(user);
    }

    @Override
    public void toNotCollectedOrders(List<OrderDto> orders)
            throws ServiceException {
        log.debug(RECEIVED_ORDERS_LIST_LOG_MESSAGE, orders);
        if (orders == null) {
            throw new ServiceException(ORDERS_CANT_BE_NULL_MESSAGE);
        }
        for (OrderDto order : orders) {
            toNotCollectedOrder(order);
        }
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

    private void completeOrder(OrderDto orderDto, Timestamp timestamp)
            throws ServiceException {
        log.debug(RECEIVED_ORDER_DTO_LOG_MESSAGE, orderDto);
        if (!validateOrderDto(orderDto)) {
            throw new ServiceException(ORDER_DTO_IS_INVALID_MESSAGE + orderDto);
        }
        if (orderDto.getStatus().equals(COMPLETED)) {
            return;
        }
        Long totalPrice = orderDto.getTotalPrice();
        Long accruedPoints = Math.round(totalPrice * FIVE_PERCENT);

        orderDto.setStatus(COMPLETED);
        orderDto.setActualRetrieveDate(timestamp);
        orderDto.setAccruedPoints(accruedPoints);

        orderService.update(orderDto);

        UserDto user = orderDto.getUser();
        Long currentPoints = user.getPoints();
        Long points = currentPoints + accruedPoints;
        user.setPoints(points);

        userService.update(user);
    }

    private boolean validateOrderDto(OrderDto orderDto) {
        if (orderDto == null || orderDto.getUser() == null) {
            return false;
        }
        String status = orderDto.getStatus();
        Long debitedPoints = orderDto.getDebitedPoints();
        Long accruedPoints = orderDto.getAccruedPoints();
        Long totalPrice = orderDto.getTotalPrice();
        Long points = orderDto.getUser().getPoints();

        return status != null
                && debitedPoints != null
                && accruedPoints != null
                && totalPrice != null
                && points != null;
    }
}
