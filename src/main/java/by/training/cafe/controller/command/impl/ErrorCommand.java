package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUrl;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.Dispatch.DispatchType;
import by.training.cafe.util.JspPathUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * The class {@code ErrorCommand} is a class that
 * implements {@link Command}.<br/>
 * Forwards to the error page if error occurred after post method.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class ErrorCommand implements Command {

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
                CommonAttributes.IS_ERROR_OCCURRED);
        if (isErrorOccurred == null) {
            return REDIRECT_HOME;
        }
        session.removeAttribute(CommonAttributes.IS_ERROR_OCCURRED);
        replaceAttributeToRequest(session, request,
                CommonAttributes.ERROR_MESSAGE_KEY);
        Optional<Object> maybeErrorCode = replaceAttributeToRequest(
                session, request, CommonAttributes.ERROR_STATUS);
        maybeErrorCode.map(Integer.class::cast)
                .ifPresent(response::setStatus);
        return FORWARD_ERROR;
    }

    private Optional<Object> replaceAttributeToRequest(HttpSession session,
                                                       HttpServletRequest request,
                                                       String attributeKey) {
        Object attribute = session.getAttribute(attributeKey);
        if (attribute != null) {
            session.removeAttribute(attributeKey);
            request.setAttribute(attributeKey, attribute);
        }
        return Optional.ofNullable(attribute);
    }
}
