package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.OrderedDishDto;
import by.training.cafe.service.OrderService;
import by.training.cafe.service.OrderedDishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class OrderedDishesAsyncCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(OrderedDishesAsyncCommand.class);
    private static final Dispatch RESULT = new Dispatch();
    private static final String APPLICATION_JSON = "application/json";

    private final Gson gson;
    private final ServiceFactory serviceFactory;

    public OrderedDishesAsyncCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        gson = new Gson();
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String method = request.getMethod();
            if (!method.equals(HttpMethod.POST.name())) {
                response.sendError(HTTP_BAD_METHOD);
                return RESULT;
            }
            String id = request.getParameter(CommonAttributes.ORDER_ID);
            log.debug("Received orderId: {}", id);
            Long orderId = Long.valueOf(id);
            OrderedDishService orderedDishService
                    = serviceFactory.getService(OrderedDishService.class);
            OrderService orderService
                    = serviceFactory.getService(OrderService.class);

            Optional<OrderDto> mayBeOrder = orderService.findById(orderId);
            if (mayBeOrder.isEmpty()) {
                response.sendError(HttpURLConnection.HTTP_BAD_REQUEST);
                return RESULT;
            }
            OrderDto orderDto = mayBeOrder.get();
            List<OrderedDishDto> orderedDishes
                    = orderedDishService.findByOrderDto(orderDto);
            String json = gson.toJson(orderedDishes);

            try (PrintWriter out = response.getWriter()) {
                response.setContentType(APPLICATION_JSON);
                out.print(json);
                out.flush();
            }
        } catch (ServiceException | IOException e) {
            log.error("Exception occurred", e);
            response.setStatus(HTTP_INTERNAL_ERROR);
        }
        return RESULT;
    }
}
