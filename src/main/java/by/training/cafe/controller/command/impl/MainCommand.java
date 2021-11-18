package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.Dispatch;
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
    private static final int INTERNAL_SERVER_ERROR_STATUS = 500;
    private static final int METHOD_NOT_ALLOWED_STATUS = 405;
    private static final String POST_METHOD = "POST";

    private final ServiceFactory serviceFactory;

    public MainCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        if (request.getMethod().equals(POST_METHOD)) {
            response.setStatus(METHOD_NOT_ALLOWED_STATUS);
            request.setAttribute("errorStatus", METHOD_NOT_ALLOWED_STATUS);
            return ERROR;
        }
        try {
            DishService service = serviceFactory.getService(DishService.class);
            String search = request.getParameter("q");
            List<DishDto> dishes;
            if (search != null) {
                dishes = service.findByNameOrDescriptionLike(search);
                request.setAttribute("searchString", search);
            } else {
                dishes = service.findAll();
            }
            Map<String, List<DishDto>> groupedDishes
                    = service.groupByCategory(dishes);
            request.setAttribute("groupedDishes", groupedDishes);
            return SUCCESS;
        } catch (ServiceException e) {
            log.error(e);
            if (e.getMessage().startsWith("String is invalid")) {
                request.setAttribute("errorMessageKey", "main.error.search");
            } else {
                response.setStatus(INTERNAL_SERVER_ERROR_STATUS);
                request.setAttribute("errorStatus", INTERNAL_SERVER_ERROR_STATUS);
            }
            return ERROR;
        }
    }
}
