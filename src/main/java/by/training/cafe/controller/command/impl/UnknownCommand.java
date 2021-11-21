package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.util.JspPathUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;

/**
 * The class {@code UnknownCommand} is a class that
 * implements {@link Command}.<br/>
 * Executes when an unknown command is received.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class UnknownCommand implements Command {

    private static final Dispatch RESULT = new Dispatch(
            Dispatch.DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final String ERROR_MESSAGE_ATTRIBUTE_KEY
            = "errorMessageKey";
    private static final String ERROR_STATUS_ATTRIBUTE_KEY
            = "errorStatus";
    public static final String NOTHING_FOUND_KEY = "main.nothingFound";

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        request.setAttribute(ERROR_STATUS_ATTRIBUTE_KEY,
                HttpURLConnection.HTTP_NOT_FOUND);
        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_KEY,
                NOTHING_FOUND_KEY);
        return RESULT;
    }
}
