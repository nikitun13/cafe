package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.service.OrderService;
import by.training.cafe.service.PaginationService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

/**
 * The class {@code AdminOrdersCommand} is a class that
 * implements {@link Command}.<br/>
 * Provides all orders to admin.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class AdminOrdersCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(AdminOrdersCommand.class);
    private static final Dispatch SUCCESS = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("admin-orders"));
    private static final Dispatch ERROR = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final int DEFAULT_LIMIT = 10;

    private final ServiceFactory serviceFactory;

    public AdminOrdersCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        HttpSession session = request.getSession();
        if (!method.equals(HttpMethod.GET.name())) {
            session.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_BAD_METHOD);
            return ERROR;
        }
        replaceAttributeToRequest(session, request,
                CommonAttributes.ERROR_MESSAGE_KEY);
        replaceAttributeToRequest(session, request,
                CommonAttributes.SUCCESS_MESSAGE_KEY);
        OrderService orderService
                = serviceFactory.getService(OrderService.class);
        try {
            Long totalOrders = orderService.countOrders();
            if (totalOrders > 0) {
                long offset = calculateOffsetAndSetPageAttributesToRequest(
                        totalOrders, request);
                List<OrderDto> orders = orderService.findAll(
                        DEFAULT_LIMIT, offset);
                request.setAttribute(CommonAttributes.ORDERS, orders);
            }
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
            session.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_INTERNAL_ERROR);
            return ERROR;
        }
        return SUCCESS;
    }

    private long calculateOffsetAndSetPageAttributesToRequest(
            long totalOrders, HttpServletRequest request) {
        PaginationService paginationService
                = serviceFactory.getService(PaginationService.class);
        long totalPages = paginationService.calculateTotalPages(
                totalOrders, DEFAULT_LIMIT);
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
        Map.Entry<Long, Long> entry = paginationService.calculateStartAndEndPage(
                currentPage, totalPages, 2);
        long startPage = entry.getKey();
        long endPage = entry.getValue();
        long offset = paginationService.calculateOffset(
                DEFAULT_LIMIT, currentPage);

        request.setAttribute(CommonAttributes.PAGE_COUNT, totalPages);
        request.setAttribute(CommonAttributes.CURRENT_PAGE, currentPage);
        request.setAttribute(CommonAttributes.START_PAGE, startPage);
        request.setAttribute(CommonAttributes.END_PAGE, endPage);
        log.debug("attribute currentPage = {}", currentPage);
        return offset;
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
