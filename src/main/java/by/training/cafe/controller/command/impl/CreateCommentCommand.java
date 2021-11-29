package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.CommentDto;
import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.service.CommentService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * The class {@code CreateCommentCommand} is a class that
 * implements {@link Command}.<br/>
 * Creates new comments.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class CreateCommentCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(CreateCommentCommand.class);
    private static final String REDIRECT_PATH = CommandUri.DISH_PAGE + "?id=%d";
    private static final Dispatch ERROR = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final String INVALID_COMMENT_BODY_MESSAGE_KEY
            = "dish.error.messageBody";
    private static final String COMMENT_DTO_IS_INVALID_MESSAGE
            = "CommentDto is invalid";
    private static final String COMMENT_ADDED_MESSAGE_KEY
            = "dish.success.commendAdded";
    private static final String BODY_PARAMETER_KEY = "body";
    private static final String RATING_PARAMETER_KEY = "rating";
    private static final String CURRENT_DISH_ATTRIBUTE_KEY = "currentDish";

    private final ServiceFactory serviceFactory;

    public CreateCommentCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        HttpSession session = request.getSession();
        if (!method.equals(HttpMethod.POST.name())) {
            session.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_BAD_METHOD);
            return ERROR;
        }
        return doPost(request);
    }

    private Dispatch doPost(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserDto user = (UserDto) session.getAttribute(CommonAttributes.USER);
        DishDto dish = (DishDto) session.getAttribute(
                CURRENT_DISH_ATTRIBUTE_KEY);
        if (user == null || dish == null) {
            log.error("User or dish is null. User: {}. Dish: {}", user, dish);
            return badRequest(session);
        }

        try {
            Short rating = Short.valueOf(
                    request.getParameter(RATING_PARAMETER_KEY));
            String body = request.getParameter(BODY_PARAMETER_KEY);

            CommentService commentService
                    = serviceFactory.getService(CommentService.class);

            CommentDto comment = CommentDto.builder()
                    .dish(dish)
                    .user(user)
                    .rating(rating)
                    .body(body)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            commentService.create(comment);
            session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                    COMMENT_ADDED_MESSAGE_KEY);
            return new Dispatch(
                    DispatchType.REDIRECT,
                    REDIRECT_PATH.formatted(dish.getId()));
        } catch (ServiceException | NumberFormatException e) {
            log.error("Exception occurred", e);
            if (e.getMessage().startsWith(COMMENT_DTO_IS_INVALID_MESSAGE)
                    || e.getClass().equals(NumberFormatException.class)) {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        INVALID_COMMENT_BODY_MESSAGE_KEY);
                return new Dispatch(DispatchType.REDIRECT,
                        REDIRECT_PATH.formatted(dish.getId()));
            }
            return badRequest(session);
        }
    }

    private Dispatch badRequest(HttpSession session) {
        session.setAttribute(CommonAttributes.IS_ERROR_OCCURRED, Boolean.TRUE);
        session.setAttribute(CommonAttributes.ERROR_STATUS,
                HTTP_BAD_REQUEST);
        return ERROR;
    }
}
