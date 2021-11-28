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
 * The class {@code AdminUpdateDishCommand} is a class that
 * implements {@link Command}.<br/>
 * Updates dish's name, price and category.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class AdminUpdateDishCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(AdminUpdateDishCommand.class);
    private static final Dispatch BAD_METHOD_ERROR = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final Dispatch REDIRECT_TO_ADMIN_DISHES = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ADMIN_DISHES);
    private static final String CHECK_INPUT_MESSAGE_KEY
            = "cafe.error.checkData";
    private static final String UPDATED_MESSAGE_KEY
            = "admin.dishes.update.success";
    private static final String DISH_ID = "dishId";
    private static final String PRICE = "price";
    private static final String NAME = "name";
    private static final String CATEGORY = "category";

    private final ServiceFactory serviceFactory;

    public AdminUpdateDishCommand(ServiceFactory serviceFactory) {
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
            String name = request.getParameter(NAME);
            String category = request.getParameter(CATEGORY);
            String priceParameter = request.getParameter(PRICE);
            long price = (long) (Double.parseDouble(priceParameter) * 100);

            DishService dishService
                    = serviceFactory.getService(DishService.class);
            Optional<DishDto> maybeDish = dishService.findById(userId);

            if (maybeDish.isPresent()) {
                DishDto dish = maybeDish.get();
                dish.setName(name);
                dish.setCategory(category);
                dish.setPrice(price);

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
