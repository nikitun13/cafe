package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.DishDto;
import by.training.cafe.service.DishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

/**
 * The class {@code MainCommand} is a class that
 * implements {@link Command}.<br/>
 * Provides menu to user.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class MainCommand implements Command {

    private static final Logger log = LogManager.getLogger(MainCommand.class);
    private static final Dispatch SUCCESS = new Dispatch(
            Dispatch.DispatchType.FORWARD,
            JspPathUtil.getPath("main"));
    private static final Dispatch ERROR = new Dispatch(
            Dispatch.DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final String SEARCH_STRING_ATTRIBUTE_KEY = "searchString";
    private static final String GROUPED_DISHES_ATTRIBUTE_KEY = "groupedDishes";
    private static final String STRING_IS_INVALID_MESSAGE = "String is invalid";
    private static final String SEARCH_STRING_IS_INVALID_KEY = "main.error.search";
    private static final String SEARCH_PARAMETER_KEY = "q";


    private final ServiceFactory serviceFactory;

    public MainCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        if (!method.equals(HttpMethod.GET.name())) {
            response.setStatus(HTTP_BAD_METHOD);
            request.setAttribute(CommonAttributes.ERROR_STATUS, HTTP_BAD_METHOD);
            return ERROR;
        }
        try {
            DishService service = serviceFactory.getService(DishService.class);
            String search = request.getParameter(SEARCH_PARAMETER_KEY);
            List<DishDto> dishes;
            if (search != null) {
                search = search.strip();
                dishes = service.findByNameOrDescriptionLike(search);
                request.setAttribute(SEARCH_STRING_ATTRIBUTE_KEY, search);
            } else {
                dishes = service.findAll();
            }
            Map<String, List<DishDto>> groupedDishes
                    = service.groupByCategory(dishes);
            request.setAttribute(GROUPED_DISHES_ATTRIBUTE_KEY, groupedDishes);
            return SUCCESS;
        } catch (ServiceException e) {
            log.error("ServiceException occurred", e);
            if (e.getMessage().startsWith(STRING_IS_INVALID_MESSAGE)) {
                request.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        SEARCH_STRING_IS_INVALID_KEY);
            } else {
                response.setStatus(HTTP_INTERNAL_ERROR);
                request.setAttribute(CommonAttributes.ERROR_STATUS,
                        HTTP_INTERNAL_ERROR);
            }
            return ERROR;
        }
    }
}
