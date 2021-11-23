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

import java.util.List;

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
    public static final String INVALID_PARAMS_FORMAT_MESSAGE
            = "Retrieved params are invalid. " +
            "CreateOrderDto: %s. OrderedDishes: %s";

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
