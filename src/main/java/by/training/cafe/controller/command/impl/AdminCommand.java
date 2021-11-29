package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.DishDto;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.service.StatisticsService;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

/**
 * The class {@code AdminCommand} is a class that
 * implements {@link Command}.<br/>
 * Provides main admin page with statistics.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class AdminCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(AdminCommand.class);
    private static final long DEFAULT_LIMIT = 3L;
    private static final Dispatch ERROR = new Dispatch(
            Dispatch.DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch SUCCESS = new Dispatch(
            Dispatch.DispatchType.FORWARD,
            JspPathUtil.getPath("admin"));
    private static final String TOP_DISHES = "topDishes";
    private static final String EARNED_TOTAL = "earnedTotal";
    private static final String EARNED_LAST_MONTH = "earnedLastMonth";
    private static final String EARNED_THIS_MONTH = "earnedThisMonth";
    private static final String START_DATE_OF_THIS_MONTH = "startDateOfThisMonth";
    private static final String END_DATE_OF_THIS_MONTH = "endDateOfThisMonth";
    private static final String START_DATE_OF_PREVIOUS_MONTH
            = "startDateOfPreviousMonth";
    private static final String END_DATE_OF_PREVIOUS_MONTH
            = "endDateOfPreviousMonth";

    private final ServiceFactory serviceFactory;

    public AdminCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        if (!method.equals(HttpMethod.GET.name())) {
            request.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_BAD_METHOD);
            return ERROR;
        }
        StatisticsService statisticsService
                = serviceFactory.getService(StatisticsService.class);
        LocalDate now = LocalDate.now();
        try {
            Date startDateOfThisMonth = Date.valueOf(
                    statisticsService.getFirstDayOfMonth(now));
            Date endDateOfThisMonth = Date.valueOf(now);
            LocalDate previousMonth = now.minusMonths(1);
            Date startDateOfPreviousMonth = Date.valueOf(
                    statisticsService.getFirstDayOfMonth(previousMonth));
            Date endDateOfPreviousMonth = Date.valueOf(
                    statisticsService.getLastDayOfMonth(previousMonth));
            long earnedTotal = statisticsService.earnedTotal();
            long earnedLastMonth = statisticsService.earnedLastMonth();
            long earnedThisMonth = statisticsService.earnedThisMonth();
            Map<DishDto, Map.Entry<Long, Long>> topDishes
                    = statisticsService.findTopDishes(DEFAULT_LIMIT);

            request.setAttribute(TOP_DISHES, topDishes);
            request.setAttribute(EARNED_TOTAL, earnedTotal);
            request.setAttribute(EARNED_LAST_MONTH, earnedLastMonth);
            request.setAttribute(EARNED_THIS_MONTH, earnedThisMonth);
            request.setAttribute(START_DATE_OF_THIS_MONTH, startDateOfThisMonth);
            request.setAttribute(END_DATE_OF_THIS_MONTH, endDateOfThisMonth);
            request.setAttribute(START_DATE_OF_PREVIOUS_MONTH,
                    startDateOfPreviousMonth);
            request.setAttribute(END_DATE_OF_PREVIOUS_MONTH,
                    endDateOfPreviousMonth);

            return SUCCESS;
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
            request.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_INTERNAL_ERROR);
            return ERROR;
        }
    }
}
