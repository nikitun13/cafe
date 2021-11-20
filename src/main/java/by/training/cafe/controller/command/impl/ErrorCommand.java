package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUrl;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.Dispatch.DispatchType;
import by.training.cafe.util.JspPathUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The class {@code ErrorCommand} is a class that
 * implements {@link Command}.<br/>
 * Forwards to the error page if error occurred after post method.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class ErrorCommand implements Command {

    private static final String IS_ERROR_OCCURRED_ATTRIBUTE_KEY
            = "isErrorOccurred";
    private static final String ERROR_MESSAGE_ATTRIBUTE_KEY
            = "errorMessageKey";
    private static final String ERROR_STATUS_ATTRIBUTE_KEY
            = "errorStatus";

    private static final Dispatch REDIRECT_HOME = new Dispatch(
            DispatchType.REDIRECT,
            CommandUrl.MAIN);
    private static final Dispatch FORWARD_ERROR = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        HttpSession session = request.getSession();
        Object isErrorOccurred = session.getAttribute(
                IS_ERROR_OCCURRED_ATTRIBUTE_KEY);
        if (isErrorOccurred == null) {
            return REDIRECT_HOME;
        }
        session.removeAttribute(IS_ERROR_OCCURRED_ATTRIBUTE_KEY);
        Object errorMessageKey = session.getAttribute(
                ERROR_MESSAGE_ATTRIBUTE_KEY);
        if (errorMessageKey != null) {
            session.removeAttribute(ERROR_MESSAGE_ATTRIBUTE_KEY);
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_KEY, errorMessageKey);
        }
        Integer errorStatus = ((Integer) session.getAttribute(
                ERROR_STATUS_ATTRIBUTE_KEY));
        if (errorStatus != null) {
            session.removeAttribute(ERROR_STATUS_ATTRIBUTE_KEY);
            response.setStatus(errorStatus);
            request.setAttribute(ERROR_STATUS_ATTRIBUTE_KEY, errorStatus);
        }
        return FORWARD_ERROR;
    }
}
