package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUrl;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

/**
 * The class {@code SignOutCommand} is a class that
 * implements {@link Command}.<br/>
 * Executes when the user signs out.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class SignOutCommand implements Command {

    private static final Logger log = LogManager.getLogger(SignOutCommand.class);
    private static final Dispatch METHOD_NOT_ALLOWED = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch SUCCESS = new Dispatch(
            DispatchType.REDIRECT,
            CommandUrl.MAIN);

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String method = request.getMethod();
            return switch (HttpMethod.valueOf(method)) {
                case GET -> methodNotAllowed(request, response);
                case POST -> doPost(request);
            };
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException occurred", e);
            return methodNotAllowed(request, response);
        }
    }

    private Dispatch doPost(HttpServletRequest request) {
        request.getSession().invalidate();
        return SUCCESS;
    }

    private Dispatch methodNotAllowed(HttpServletRequest request,
                                      HttpServletResponse response) {
        response.setStatus(HTTP_BAD_METHOD);
        request.setAttribute(CommonAttributes.ERROR_STATUS, HTTP_BAD_METHOD);
        return METHOD_NOT_ALLOWED;
    }
}
