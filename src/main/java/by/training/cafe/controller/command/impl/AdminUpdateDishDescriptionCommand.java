package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.Dispatch.DispatchType;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.DishDto;
import by.training.cafe.service.DishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

/**
 * The class {@code AdminUpdateDishDescriptionCommand} is a class that
 * implements {@link Command}.<br/>
 * Updates dish's description.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class AdminUpdateDishDescriptionCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(AdminUpdateDishDescriptionCommand.class);
    private static final Dispatch BAD_METHOD_ERROR = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final Dispatch REDIRECT_TO_ADMIN_DISHES = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ADMIN_DISHES);
    private static final String CHECK_INPUT_MESSAGE_KEY
            = "cafe.error.checkData";
    private static final String UPDATED_MESSAGE_KEY
            = "admin.dishes.update.description.success";
    private static final String DISH_ID = "dishId";
    private static final String DESCRIPTION = "description";

    private final ServiceFactory serviceFactory;

    public AdminUpdateDishDescriptionCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        HttpSession session = request.getSession();
        if (!method.equals(HttpMethod.POST.name())) {
            session.setAttribute(CommonAttributes.IS_ERROR_OCCURRED,
                    Boolean.TRUE);
            session.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_BAD_METHOD);
            return BAD_METHOD_ERROR;
        }
        try {
            Long userId = Long.valueOf(request.getParameter(DISH_ID));
            String description = request.getParameter(DESCRIPTION);

            DishService dishService
                    = serviceFactory.getService(DishService.class);
            Optional<DishDto> maybeDish = dishService.findById(userId);

            if (maybeDish.isPresent()) {
                DishDto dish = maybeDish.get();
                dish.setDescription(description);

                dishService.update(dish);

                session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                        UPDATED_MESSAGE_KEY);
                return REDIRECT_TO_ADMIN_DISHES;
            }
            log.error("Dish is empty");
        } catch (IllegalArgumentException | ServiceException e) {
            log.error("Exception occurred", e);
        }
        session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                CHECK_INPUT_MESSAGE_KEY);
        return REDIRECT_TO_ADMIN_DISHES;
    }
}
