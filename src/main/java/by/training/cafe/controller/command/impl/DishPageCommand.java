package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUrl;
import by.training.cafe.controller.command.CommonAttributes;
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
import java.util.Map.Entry;
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
    public static final String DISH_NOT_FOUND_MESSAGE_KEY
            = "dish.error.notFound";
    public static final String INVALID_COMMENT_BODY_MESSAGE_KEY
            = "dish.error.messageBody";
    public static final String COMMENT_DTO_IS_INVALID_MESSAGE
            = "CommentDto is invalid";
    public static final String COMMENT_ADDED_MESSAGE_KEY
            = "dish.success.commendAdded";
    public static final String BODY_PARAMETER_KEY = "body";
    public static final String RATING_PARAMETER_KEY = "rating";
    public static final String CURRENT_DISH_ATTRIBUTE_KEY = "currentDish";
    public static final String DISH_ATTRIBUTE_KEY = "dish";
    public static final String COUNT_GROUPED_BY_RATING_ATTRIBUTE_KEY
            = "countGroupedByRating";
    public static final String PAGE_COUNT_ATTRIBUTE_KEY = "pageCount";
    public static final String COMMENTS_ATTRIBUTE_KEY = "comments";
    public static final String AVERAGE_RATING_ATTRIBUTE_KEY = "averageRating";
    public static final String CURRENT_PAGE_ATTRIBUTE_KEY = "currentPage";
    public static final String START_PAGE_ATTRIBUTE_KEY = "startPage";
    public static final String END_PAGE_ATTRIBUTE_KEY = "endPage";
    public static final String PAGE_ATTRIBUTE_KEY = "page";
    public static final String ID_PARAMETER_KEY = "id";

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
                    CommonAttributes.ERROR_STATUS, HTTP_INTERNAL_ERROR);
        } catch (IllegalArgumentException e) {
            log.error(e);
            response.setStatus(HTTP_BAD_METHOD);
            request.setAttribute(
                    CommonAttributes.ERROR_STATUS, HTTP_BAD_METHOD);
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
        String page = request.getParameter(PAGE_ATTRIBUTE_KEY);
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
            Entry<Long, Long> entry = PaginationUtil.calculateStartAndEndPage(
                    currentPage, pageCount);
            long startPage = entry.getKey();
            long endPage = entry.getValue();
            long offset = PaginationUtil.calculateOffset(
                    DEFAULT_LIMIT, currentPage);
            List<CommentDto> comments
                    = commentService.findByDishDtoOrderByCreatedAtDesc(
                    dish, DEFAULT_LIMIT, offset);
            Double averageRating = commentService.averageDishRating(dish);

            request.setAttribute(COMMENTS_ATTRIBUTE_KEY, comments);
            request.setAttribute(AVERAGE_RATING_ATTRIBUTE_KEY, averageRating);
            request.setAttribute(CURRENT_PAGE_ATTRIBUTE_KEY, currentPage);
            request.setAttribute(START_PAGE_ATTRIBUTE_KEY, startPage);
            request.setAttribute(END_PAGE_ATTRIBUTE_KEY, endPage);
            log.debug("attribute currentPage = {}", currentPage);
        }
        request.setAttribute(DISH_ATTRIBUTE_KEY, dish);
        request.setAttribute(COUNT_GROUPED_BY_RATING_ATTRIBUTE_KEY,
                countGroupedByRating);
        request.setAttribute(PAGE_COUNT_ATTRIBUTE_KEY, pageCount);

        HttpSession session = request.getSession();
        session.setAttribute(CURRENT_DISH_ATTRIBUTE_KEY, dish);

        replaceAttributeToRequest(session, request,
                CommonAttributes.ERROR_MESSAGE_KEY);
        replaceAttributeToRequest(session, request,
                CommonAttributes.SUCCESS_MESSAGE_KEY);

        return SUCCESS_GET;
    }

    private Dispatch doPost(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserDto user = (UserDto) session.getAttribute(CommonAttributes.USER);
        DishDto dish = (DishDto) session.getAttribute(
                CURRENT_DISH_ATTRIBUTE_KEY);
        if (user == null || dish == null) {
            log.error("User or dish is null. User: {}. Dish: {}", user, dish);
            return internalErrorOccurred(session);
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
            log.error(e);
            if (e.getMessage().startsWith(COMMENT_DTO_IS_INVALID_MESSAGE)
                    || e.getClass().equals(NumberFormatException.class)) {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        INVALID_COMMENT_BODY_MESSAGE_KEY);
                return new Dispatch(DispatchType.REDIRECT,
                        REDIRECT_PATH.formatted(dish.getId()));
            }
            return internalErrorOccurred(session);
        }
    }

    private Dispatch internalErrorOccurred(HttpSession session) {
        session.setAttribute(CommonAttributes.IS_ERROR_OCCURRED, Boolean.TRUE);
        session.setAttribute(CommonAttributes.ERROR_STATUS,
                HTTP_INTERNAL_ERROR);
        return ERROR_POST;
    }

    private Optional<Long> extractIdParameter(HttpServletRequest request) {
        String id = request.getParameter(ID_PARAMETER_KEY);
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
        request.setAttribute(CommonAttributes.ERROR_STATUS, HTTP_NOT_FOUND);
        request.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                DISH_NOT_FOUND_MESSAGE_KEY);
        return ERROR_GET;
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
