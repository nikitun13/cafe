package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.*;
import by.training.cafe.controller.command.Dispatch.DispatchType;
import by.training.cafe.dto.CommentDto;
import by.training.cafe.dto.DishDto;
import by.training.cafe.service.*;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.net.HttpURLConnection.*;

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
    private static final Dispatch ERROR_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch SUCCESS_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("dish"));
    private static final String DISH_NOT_FOUND_MESSAGE_KEY
            = "dish.error.notFound";
    private static final String CURRENT_DISH_ATTRIBUTE_KEY = "currentDish";
    private static final String DISH_ATTRIBUTE_KEY = "dish";
    private static final String COUNT_GROUPED_BY_RATING_ATTRIBUTE_KEY
            = "countGroupedByRating";
    private static final String COMMENTS_ATTRIBUTE_KEY = "comments";
    private static final String AVERAGE_RATING_ATTRIBUTE_KEY = "averageRating";
    private static final String ID_PARAMETER_KEY = "id";

    private final ServiceFactory serviceFactory;

    public DishPageCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String method = request.getMethod();
            if (!method.equals(HttpMethod.GET.name())) {
                response.setStatus(HTTP_BAD_METHOD);
                request.setAttribute(CommonAttributes.ERROR_STATUS,
                        HTTP_BAD_METHOD);
                return ERROR_GET;
            }
            return doGet(request, response);
        } catch (ServiceException e) {
            log.error("ServiceException occurred", e);
            response.setStatus(HTTP_BAD_REQUEST);
            request.setAttribute(
                    CommonAttributes.ERROR_STATUS, HTTP_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException occurred", e);
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
        CommentService commentService
                = serviceFactory.getService(CommentService.class);
        Map<Short, Long> countGroupedByRating
                = commentService.countCommentsByDishGroupByRating(dish);
        Long totalComments = commentService.countByDishDto(dish);
        PaginationService paginationService
                = serviceFactory.getService(PaginationService.class);
        long totalPages = paginationService.calculateTotalPages(
                totalComments, DEFAULT_LIMIT);
        if (totalPages > 0) {
            String page = request.getParameter(CommonAttributes.PAGE);
            log.debug("Received page param = {}", page);
            long currentPage;
            try {
                currentPage = Long.parseLong(page);
                currentPage = paginationService.isValidCurrentPageOrElseGet(
                        currentPage, totalPages,
                        () -> 1L);
            } catch (NumberFormatException e) {
                log.debug("Invalid page param", e);
                currentPage = 1L;
            }
            Entry<Long, Long> entry = paginationService.calculateStartAndEndPage(
                    currentPage, totalPages, 2);
            long startPage = entry.getKey();
            long endPage = entry.getValue();
            long offset = paginationService.calculateOffset(
                    DEFAULT_LIMIT, currentPage);
            List<CommentDto> comments
                    = commentService.findByDishDtoOrderByCreatedAtDesc(
                    dish, DEFAULT_LIMIT, offset);
            Double averageRating = commentService.averageDishRating(dish);

            request.setAttribute(COMMENTS_ATTRIBUTE_KEY, comments);
            request.setAttribute(AVERAGE_RATING_ATTRIBUTE_KEY, averageRating);
            request.setAttribute(CommonAttributes.CURRENT_PAGE, currentPage);
            request.setAttribute(CommonAttributes.START_PAGE, startPage);
            request.setAttribute(CommonAttributes.END_PAGE, endPage);
            log.debug("attribute currentPage = {}", currentPage);
        }
        request.setAttribute(DISH_ATTRIBUTE_KEY, dish);
        request.setAttribute(COUNT_GROUPED_BY_RATING_ATTRIBUTE_KEY,
                countGroupedByRating);
        request.setAttribute(CommonAttributes.PAGE_COUNT, totalPages);

        HttpSession session = request.getSession();
        session.setAttribute(CURRENT_DISH_ATTRIBUTE_KEY, dish);

        replaceAttributeToRequest(session, request,
                CommonAttributes.ERROR_MESSAGE_KEY);
        replaceAttributeToRequest(session, request,
                CommonAttributes.SUCCESS_MESSAGE_KEY);

        return SUCCESS_GET;
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
            log.error("NumberFormatException occurred", e);
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
