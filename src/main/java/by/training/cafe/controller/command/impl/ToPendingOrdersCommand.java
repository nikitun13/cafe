package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.Dispatch.DispatchType;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.service.OrderProcessService;
import by.training.cafe.service.OrderService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

public class ToPendingOrdersCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(ToPendingOrdersCommand.class);
    private static final Dispatch BAD_METHOD_ERROR = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final Dispatch REDIRECT_TO_ADMIN_ORDERS = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ADMIN_ORDERS);
    private static final String SOMETHING_WENT_WRONG_MESSAGE_KEY = "error.oops";
    private static final String UPDATED_MESSAGE_KEY = "admin.order.updated";
    private static final String NOT_ENOUGH_POINTS = "Not enough points";
    private static final String NOT_ENOUGH_POINTS_MESSAGE_KEY
            = "admin.order.notEnoughPoints";

    private final ServiceFactory serviceFactory;

    public ToPendingOrdersCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        HttpSession session = request.getSession();
        if (!method.equals(HttpMethod.POST.name())) {
            session.setAttribute(CommonAttributes.ERROR_STATUS, HTTP_BAD_METHOD);
            return BAD_METHOD_ERROR;
        }
        String[] ordersId = request.getParameterValues(CommonAttributes.ORDERS);
        OrderService orderService
                = serviceFactory.getService(OrderService.class);
        List<OrderDto> orders = new ArrayList<>();
        try {
            for (String orderId : ordersId) {
                Long id = Long.valueOf(orderId);
                Optional<OrderDto> maybeOrder = orderService.findById(id);
                if (maybeOrder.isPresent()) {
                    OrderDto orderDto = maybeOrder.get();
                    orders.add(orderDto);
                }
            }
        } catch (NumberFormatException | ServiceException e) {
            log.error("Exception occurred", e);
            session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                    SOMETHING_WENT_WRONG_MESSAGE_KEY);
            return REDIRECT_TO_ADMIN_ORDERS;
        }

        OrderProcessService orderProcessService
                = serviceFactory.getService(OrderProcessService.class);
        try {
            orderProcessService.toPendingOrders(orders);
            session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                    UPDATED_MESSAGE_KEY);
        } catch (ServiceException e) {
            log.error("Exception occurred", e);
            if (e.getMessage().startsWith(NOT_ENOUGH_POINTS)) {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        NOT_ENOUGH_POINTS_MESSAGE_KEY);
            } else {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        SOMETHING_WENT_WRONG_MESSAGE_KEY);
            }
        }
        return REDIRECT_TO_ADMIN_ORDERS;
    }
}
