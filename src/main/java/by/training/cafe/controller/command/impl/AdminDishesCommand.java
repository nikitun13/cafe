package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.DishDto;
import by.training.cafe.entity.DishCategory;
import by.training.cafe.service.DishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

/**
 * The class {@code AdminDishesCommand} is a class that
 * implements {@link Command}.<br/>
 * Provides all dishes to admin.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class AdminDishesCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(AdminDishesCommand.class);
    private static final Dispatch SUCCESS = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("admin-dishes"));
    private static final Dispatch ERROR = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final String DEFAULT_CATEGORY = "PIZZA";
    private static final String CATEGORY = "category";
    private static final String DISHES = "dishes";
    private static final String CATEGORIES = "categories";
    private static final String CURRENT_CATEGORY = "currentCategory";

    private final ServiceFactory serviceFactory;

    public AdminDishesCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        HttpSession session = request.getSession();
        if (!method.equals(HttpMethod.GET.name())) {
            request.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_BAD_METHOD);
            return ERROR;
        }
        replaceAttributeToRequest(session, request,
                CommonAttributes.ERROR_MESSAGE_KEY);
        replaceAttributeToRequest(session, request,
                CommonAttributes.SUCCESS_MESSAGE_KEY);
        DishService dishService
                = serviceFactory.getService(DishService.class);
        try {
            DishCategory[] categories = DishCategory.values();
            String category = request.getParameter(CATEGORY);
            if (category != null) {
                category = category.toUpperCase();
            } else {
                category = DEFAULT_CATEGORY;
            }
            category = DishCategory.contains(category)
                    ? category
                    : DEFAULT_CATEGORY;

            List<DishDto> dishes = dishService.findByCategory(category);

            request.setAttribute(DISHES, dishes);
            request.setAttribute(CATEGORIES, categories);
            request.setAttribute(CURRENT_CATEGORY, category);
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
            request.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_INTERNAL_ERROR);
            return ERROR;
        }
        return SUCCESS;
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
