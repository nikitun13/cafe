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
import by.training.cafe.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

/**
 * The class {@code AdminCreateDishCommand} is a class that
 * implements {@link Command}.<br/>
 * Creates new dish.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class AdminCreateDishCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(AdminCreateDishCommand.class);
    private static final Dispatch BAD_METHOD_ERROR = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final Dispatch REDIRECT_TO_ADMIN_DISHES = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ADMIN_DISHES);
    private static final String CHECK_INPUT_MESSAGE_KEY
            = "cafe.error.checkData";
    private static final String UPDATED_MESSAGE_KEY
            = "admin.dishes.create.success";
    private static final String PRICE = "price";
    private static final String NAME = "name";
    private static final String CATEGORY = "category";
    private static final String IMAGE = "image";
    private static final String DESCRIPTION = "description";
    private static final String DISH_IMAGE_PATH_FORMAT = "dishes/dish-%d.png";
    private static final String IMAGES_PATH
            = PropertiesUtil.get("img.basePath");

    private final ServiceFactory serviceFactory;

    public AdminCreateDishCommand(ServiceFactory serviceFactory) {
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
        DishService dishService
                = serviceFactory.getService(DishService.class);
        try {
            String name = request.getParameter(NAME);
            String category = request.getParameter(CATEGORY);
            String priceParameter = request.getParameter(PRICE);
            long price = (long) (Double.parseDouble(priceParameter) * 100);
            String description = request.getParameter(DESCRIPTION);

            DishDto dish = DishDto.builder()
                    .name(name)
                    .category(category)
                    .price(price)
                    .description(description)
                    .build();

            dishService.create(dish);

            if (saveImage(request, dish, dishService)) {
                session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                        UPDATED_MESSAGE_KEY);
                return REDIRECT_TO_ADMIN_DISHES;
            }
        } catch (IllegalArgumentException | ServiceException e) {
            log.error("Exception occurred", e);
        }
        session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                CHECK_INPUT_MESSAGE_KEY);
        return REDIRECT_TO_ADMIN_DISHES;
    }

    private boolean saveImage(HttpServletRequest request, DishDto dish,
                              DishService dishService) throws ServiceException {
        try {
            Part image = request.getPart(IMAGE);
            try (InputStream inputStream = image.getInputStream()) {
                Path path = buildPath(request, dish.getId());
                Files.write(path, inputStream.readAllBytes());
            }
            return true;
        } catch (IOException | ServletException | InvalidPathException e) {
            log.error("Exception occurred during saving file", e);
            dishService.delete(dish);
            HttpSession session = request.getSession();
            session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                    CHECK_INPUT_MESSAGE_KEY);
            return false;
        }
    }

    private Path buildPath(HttpServletRequest request, Long dishId) {
        String rootPath = request.getServletContext().getRealPath("");
        return Path.of(rootPath)
                .getParent()
                .resolve(IMAGES_PATH)
                .resolve(DISH_IMAGE_PATH_FORMAT.formatted(dishId));
    }
}
