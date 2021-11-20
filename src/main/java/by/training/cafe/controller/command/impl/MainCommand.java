package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
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
            request.setAttribute("errorStatus", HTTP_BAD_METHOD);
            return ERROR;
        }
        try {
            DishService service = serviceFactory.getService(DishService.class);
            String search = request.getParameter("q");
            List<DishDto> dishes;
            if (search != null) {
                search = search.strip();
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
                response.setStatus(HTTP_INTERNAL_ERROR);
                request.setAttribute("errorStatus", HTTP_INTERNAL_ERROR);
            }
            return ERROR;
        }
    }
}
