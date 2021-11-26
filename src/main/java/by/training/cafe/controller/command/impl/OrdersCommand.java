package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.OrderedDishDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.service.OrderProcessService;
import by.training.cafe.service.OrderService;
import by.training.cafe.service.OrderedDishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.service.UserService;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

public class OrdersCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(OrdersCommand.class);
    private static final Dispatch ERROR_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch FORWARD_TO_ORDERS = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("orders"));
    private static final Dispatch REDIRECT_TO_ORDERS = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ORDERS);
    private static final String IS_DELETABLE_ORDERS_MAP
            = "isDeletableOrdersMap";
    private static final Duration FIVE_MINUTES
            = Duration.of(5L, ChronoUnit.MINUTES);
    private static final String ERROR_OCCURRED_KEY = "error.oops";
    private static final String ORDER_CANT_BE_DELETED_KEY
            = "profile.order.delete.error";
    private static final String ORDER_DELETED_KEY = "profile.order.delete";

    private final ServiceFactory serviceFactory;

    public OrdersCommand(ServiceFactory serviceFactory) {
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
        try {
            Long orderId = Long.valueOf(
                    request.getParameter(CommonAttributes.ORDER_ID));
            OrderService orderService
                    = serviceFactory.getService(OrderService.class);
            Optional<OrderDto> maybeOrder = orderService.findById(orderId);
            if (maybeOrder.isPresent()) {
                OrderDto orderDto = maybeOrder.get();
                OrderProcessService orderProcessService
                        = serviceFactory.getService(OrderProcessService.class);
                orderProcessService.deleteNewOrder(orderDto);
                UserDto user = orderDto.getUser();
                session.setAttribute(CommonAttributes.USER, user);

                session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                        ORDER_DELETED_KEY);
                return REDIRECT_TO_ORDERS;
            }
        } catch (NumberFormatException | ServiceException e) {
            log.error("Exception occurred", e);
        }
        session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                ORDER_CANT_BE_DELETED_KEY);
        return REDIRECT_TO_ORDERS;
    }

    private Dispatch doGet(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserDto user = (UserDto) session.getAttribute(CommonAttributes.USER);
        UserService userService = serviceFactory.getService(UserService.class);
        try {
            Optional<UserDto> maybeUser = userService.findById(user.getId());
            if (maybeUser.isPresent()) {
                user = maybeUser.get();
                session.setAttribute(CommonAttributes.USER, user);
            }
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
        }
        replaceAttributeToRequest(session, request,
                CommonAttributes.ERROR_MESSAGE_KEY);
        replaceAttributeToRequest(session, request,
                CommonAttributes.SUCCESS_MESSAGE_KEY);
        replaceAttributeToRequest(session, request,
                CommonAttributes.ORDER_CREATED);
        try {
            OrderService orderService
                    = serviceFactory.getService(OrderService.class);
            List<OrderDto> orders = orderService.findByUserDto(user);
            request.setAttribute(CommonAttributes.ORDERS, orders);
            if (orders.isEmpty()) {
                return FORWARD_TO_ORDERS;
            }
            OrderedDishService orderedDishService
                    = serviceFactory.getService(OrderedDishService.class);
            OrderProcessService orderProcessService
                    = serviceFactory.getService(OrderProcessService.class);
            OrderDto orderDto = orders.get(0);
            List<OrderedDishDto> orderedDishes
                    = orderedDishService.findByOrderDto(orderDto);
            Map<OrderDto, Boolean> isDeletableOrdersMap
                    = orderProcessService.isDeletableOrders(orders, FIVE_MINUTES);

            request.setAttribute(IS_DELETABLE_ORDERS_MAP, isDeletableOrdersMap);
            request.setAttribute(CommonAttributes.ORDERED_DISHES, orderedDishes);

        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
            request.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                    ERROR_OCCURRED_KEY);
        }
        return FORWARD_TO_ORDERS;
    }

    private void replaceAttributeToRequest(HttpSession session,
                                           HttpServletRequest request,
                                           String attributeKey) {
        Object attribute = session.getAttribute(attributeKey);
        if (attribute != null) {
            session.removeAttribute(attributeKey);
            request.setAttribute(attributeKey, attribute);
        }
    }
}
