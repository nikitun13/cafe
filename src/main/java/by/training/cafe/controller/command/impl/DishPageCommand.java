package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUrl;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.Dispatch.DispatchType;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.CommentDto;
import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.service.CommentService;
import by.training.cafe.service.DishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.util.JspPathUtil;
import by.training.cafe.util.PaginationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

/**
 * The class {@code DishPageCommand} is a class that
 * implements {@link Command}.<br/>
 * Provides dish info and comments to user.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class DishPageCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(DishPageCommand.class);
    private static final long DEFAULT_LIMIT = 5L;
    private static final String ERROR_STATUS_ATTRIBUTE_KEY = "errorStatus";
    private static final String ERROR_MESSAGE_ATTRIBUTE_KEY = "errorMessageKey";
    private static final String REDIRECT_PATH = CommandUrl.DISH_PAGE + "?id=%d";

    private static final Dispatch ERROR_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch ERROR_POST = new Dispatch(
            DispatchType.REDIRECT,
            CommandUrl.ERROR);
    private static final Dispatch SUCCESS_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("dish"));
    private static final String SUCCESS_MESSAGE_ATTRIBUTE_KEY
            = "successMessageKey";

    private final ServiceFactory serviceFactory;

    public DishPageCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String method = request.getMethod();
            return switch (HttpMethod.valueOf(method)) {
                case GET -> doGet(request, response);
                case POST -> doPost(request);
            };
        } catch (ServiceException e) {
            log.error(e);
            response.setStatus(HTTP_INTERNAL_ERROR);
            request.setAttribute(
                    ERROR_STATUS_ATTRIBUTE_KEY, HTTP_INTERNAL_ERROR);
        } catch (IllegalArgumentException e) {
            log.error(e);
            response.setStatus(HTTP_BAD_METHOD);
            request.setAttribute(
                    ERROR_STATUS_ATTRIBUTE_KEY, HTTP_BAD_METHOD);
        }
        return ERROR_GET;
    }

    private Dispatch doGet(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServiceException {
        Optional<Long> maybeId = extractIdParameter(request);
        if (maybeId.isEmpty()) {
            return dishNotFound(request, response);
        }

        Long dishId = maybeId.get();
        DishService dishService = serviceFactory.getService(DishService.class);
        Optional<DishDto> maybeDish = dishService.findById(dishId);
        if (maybeDish.isEmpty()) {
            return dishNotFound(request, response);
        }

        DishDto dish = maybeDish.get();
        String page = request.getParameter("page");
        log.debug("Received page param = {}", page);
        long currentPage = PaginationUtil.parsePageOrElseDefault(page);
        CommentService commentService
                = serviceFactory.getService(CommentService.class);
        Map<Short, Long> countGroupedByRating
                = commentService.countCommentsByDishGroupByRating(dish);
        Long totalComments = commentService.countByDishDto(dish);
        long pageCount = PaginationUtil.calculateNumberOfPages(
                totalComments, DEFAULT_LIMIT);
        if (pageCount > 0) {
            currentPage = PaginationUtil.checkCurrentPageIsInRangeOfTotalPagesOrElseDefault(
                    currentPage, pageCount);
            long offset = PaginationUtil.calculateOffset(
                    DEFAULT_LIMIT, currentPage);
            List<CommentDto> comments
                    = commentService.findByDishDtoOrderByCreatedAtDesc(
                    dish, DEFAULT_LIMIT, offset);
            Double averageRating = commentService.averageDishRating(dish);

            request.setAttribute("comments", comments);
            request.setAttribute("averageRating", averageRating);
            request.setAttribute("currentPage", currentPage);
            log.debug("attribute currentPage = {}", currentPage);
        }
        request.setAttribute("dish", dish);
        request.setAttribute("countGroupedByRating", countGroupedByRating);
        request.setAttribute("pageCount", pageCount);

        HttpSession session = request.getSession();
        session.setAttribute("currentDish", dish);

        Object errorMessageKey = session.getAttribute(
                ERROR_MESSAGE_ATTRIBUTE_KEY);
        Object successMessageKey = session.getAttribute(
                SUCCESS_MESSAGE_ATTRIBUTE_KEY);
        if (errorMessageKey != null) {
            session.removeAttribute(ERROR_MESSAGE_ATTRIBUTE_KEY);
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_KEY,
                    errorMessageKey);
        } else if (successMessageKey != null) {
            session.removeAttribute(SUCCESS_MESSAGE_ATTRIBUTE_KEY);
            request.setAttribute(SUCCESS_MESSAGE_ATTRIBUTE_KEY,
                    successMessageKey);
        }
        return SUCCESS_GET;
    }

    private Dispatch doPost(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserDto user = (UserDto) session.getAttribute("user");
        DishDto dish = (DishDto) session.getAttribute("currentDish");
        if (user == null || dish == null) {
            log.error("User or dish is null. User: {}. Dish: {}", user, dish);
            return internalErrorOccurred(session);
        }

        try {
            Short rating = Short.valueOf(request.getParameter("rating"));
            String body = request.getParameter("body");

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
            session.setAttribute(SUCCESS_MESSAGE_ATTRIBUTE_KEY,
                    "dish.success.commendAdded");
            return new Dispatch(
                    DispatchType.REDIRECT,
                    REDIRECT_PATH.formatted(dish.getId()));
        } catch (ServiceException | NumberFormatException e) {
            log.error(e);
            if (e.getMessage().startsWith("CommentDto is invalid")
                    || e.getClass().equals(NumberFormatException.class)) {
                session.setAttribute(ERROR_MESSAGE_ATTRIBUTE_KEY,
                        "dish.error.messageBody");
                return new Dispatch(DispatchType.REDIRECT,
                        REDIRECT_PATH.formatted(dish.getId()));
            }
            return internalErrorOccurred(session);
        }
    }

    private Dispatch internalErrorOccurred(HttpSession session) {
        session.setAttribute("isErrorOccurred", Boolean.TRUE);
        session.setAttribute(ERROR_STATUS_ATTRIBUTE_KEY, HTTP_INTERNAL_ERROR);
        return ERROR_POST;
    }

    private Optional<Long> extractIdParameter(HttpServletRequest request) {
        String id = request.getParameter("id");
        log.debug("Received id param = {}", id);
        if (id == null) {
            return Optional.empty();
        }
        try {
            long parsedId = Long.parseLong(id);
            if (parsedId > 0) {
                return Optional.of(parsedId);
            }
        } catch (NumberFormatException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    private Dispatch dishNotFound(HttpServletRequest request,
                                  HttpServletResponse response) {
        response.setStatus(HTTP_NOT_FOUND);
        request.setAttribute(ERROR_STATUS_ATTRIBUTE_KEY, HTTP_NOT_FOUND);
        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_KEY, "dish.error.notFound");
        return ERROR_GET;
    }
}
