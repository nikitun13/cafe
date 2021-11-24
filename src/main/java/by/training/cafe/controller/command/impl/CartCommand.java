package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.*;
import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.OrderedDishDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.service.DishService;
import by.training.cafe.service.OrderProcessService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

/**
 * The class {@code CartCommand} is a class that
 * implements {@link Command}.<br/>
 * Provides order creation functionality.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class CartCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(CartCommand.class);
    private static final Dispatch ERROR_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch SUCCESS_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("cart"));
    private static final Dispatch SUCCESS_POST = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ORDERS);
    private static final Dispatch ERROR_POST = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.CART);
    private static final String MIN_DATE_TIMESTAMP = "minDateTimestamp";
    private static final String MAX_DATE_TIMESTAMP = "maxDateTimestamp";
    private static final String MIN_DATE = "minDate";
    private static final String MAX_DATE = "maxDate";
    private static final String CHECK_DATA_MESSAGE_KEY = "cafe.error.checkData";
    private static final String ORDER_CREATED_KEY = "profile.order.create.success";

    private final ServiceFactory serviceFactory;

    public CartCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String method = request.getMethod();
            return switch (HttpMethod.valueOf(method)) {
                case GET -> doGet(request);
                case POST -> doPost(request);
            };
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException occurred", e);
            response.setStatus(HTTP_BAD_METHOD);
            request.setAttribute(
                    CommonAttributes.ERROR_STATUS, HTTP_BAD_METHOD);
        }
        return ERROR_GET;
    }

    private Dispatch doPost(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserDto user = (UserDto) session.getAttribute(CommonAttributes.USER);
        try {
            long debitedPoints = Long.parseLong(
                    request.getParameter(CommonAttributes.DEBITED_POINTS));
            Timestamp expectedRetrieveDate = new Timestamp(Long.parseLong(
                    request.getParameter(CommonAttributes.EXPECTED_RETRIEVE_DATE)));
            String[] pairs = request.getParameterValues(CommonAttributes.ORDERED_DISHES);
            List<OrderedDishDto> orderedDishes = createOrderedDishes(pairs);

            CreateOrderDto createOrderDto = CreateOrderDto.builder()
                    .user(user)
                    .debitedPoints(debitedPoints)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .expectedRetrieveDate(expectedRetrieveDate)
                    .build();

            OrderProcessService orderProcessService
                    = serviceFactory.getService(OrderProcessService.class);
            orderProcessService.createOrder(createOrderDto, orderedDishes);
        } catch (NumberFormatException | ServiceException e) {
            log.error("Exception occurred", e);
            session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                    CHECK_DATA_MESSAGE_KEY);
            return ERROR_POST;
        }
        session.setAttribute(CommonAttributes.ORDER_CREATED, Boolean.TRUE);
        session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY, ORDER_CREATED_KEY);
        return SUCCESS_POST;
    }

    private Dispatch doGet(HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minDateLocalDateTime = now.plus(1L, ChronoUnit.HOURS);
        LocalDateTime maxDateLocalDateTime = now.plus(1L, ChronoUnit.WEEKS);
        String minDate = minDateLocalDateTime
                .truncatedTo(ChronoUnit.MINUTES).toString();
        String maxDate = maxDateLocalDateTime
                .truncatedTo(ChronoUnit.MINUTES).toString();
        request.setAttribute(MIN_DATE, minDate);
        request.setAttribute(MAX_DATE, maxDate);
        Timestamp minDateTimestamp = Timestamp.valueOf(minDateLocalDateTime);
        Timestamp maxDateTimestamp = Timestamp.valueOf(maxDateLocalDateTime);
        request.setAttribute(MIN_DATE_TIMESTAMP, minDateTimestamp);
        request.setAttribute(MAX_DATE_TIMESTAMP, maxDateTimestamp);
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(CommonAttributes.ERROR_MESSAGE_KEY);
        if (attribute != null) {
            session.removeAttribute(CommonAttributes.ERROR_MESSAGE_KEY);
            request.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY, attribute);
        }
        return SUCCESS_GET;
    }

    private List<OrderedDishDto> createOrderedDishes(String[] pairs) {
        if (pairs == null) {
            return Collections.emptyList();
        }
        try {
            List<OrderedDishDto> orderedDishes = new ArrayList<>();
            for (String pair : pairs) {
                String[] split = pair.split("-");
                if (split.length != 2) {
                    return Collections.emptyList();
                }
                Long dishId = Long.valueOf(split[0]);
                Short dishCount = Short.valueOf(split[1]);

                DishService dishService
                        = serviceFactory.getService(DishService.class);
                Optional<DishDto> maybeDish = dishService.findById(dishId);
                if (maybeDish.isPresent()) {
                    DishDto dish = maybeDish.get();
                    OrderedDishDto orderedDish = OrderedDishDto.builder()
                            .dish(dish)
                            .dishCount(dishCount)
                            .dishPrice(dish.getPrice())
                            .build();
                    orderedDishes.add(orderedDish);
                } else {
                    log.error("Dish with id {} wasn't found", dishId);
                    return Collections.emptyList();
                }
            }
            return orderedDishes;
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
            return Collections.emptyList();
        }
    }
}
