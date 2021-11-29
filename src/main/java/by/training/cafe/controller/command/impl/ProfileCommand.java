package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.UserDto;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.service.UserService;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

/**
 * The class {@code ProfileCommand} is a class that
 * implements {@link Command}.<br/>
 * Provides user profile page and user update password
 * functionality.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class ProfileCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(ProfileCommand.class);
    private static final Dispatch ERROR_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch SUCCESS_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("profile"));
    private static final Dispatch REDIRECT_TO_PROFILE = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.PROFILE);
    private static final String OLD_PASSWORD_KEY = "oldPassword";
    private static final String NEW_PASSWORD_KEY = "newPassword";
    private static final String CHECK_DATA_MESSAGE_KEY = "cafe.error.checkData";
    private static final String UPDATE_PASSWORD_SUCCESS_KEY
            = "profile.updatePassword.success";

    private final ServiceFactory serviceFactory;

    public ProfileCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String method = request.getMethod();
            return switch (HttpMethod.valueOf(method)) {
                case GET -> doGet(request);
                case POST -> doPost(request);
            };
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException occurred", e);
            response.setStatus(HTTP_BAD_METHOD);
            request.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_BAD_METHOD);
            return ERROR_GET;
        }
    }

    private Dispatch doPost(HttpServletRequest request) {
        String oldPassword = request.getParameter(OLD_PASSWORD_KEY);
        String newPassword = request.getParameter(NEW_PASSWORD_KEY);
        String repeatPassword = request.getParameter(
                CommonAttributes.REPEAT_PASSWORD);

        HttpSession session = request.getSession();
        UserDto user = (UserDto) session.getAttribute(CommonAttributes.USER);

        UserService userService = serviceFactory.getService(UserService.class);
        try {
            if (userService.updatePassword(user, oldPassword,
                    newPassword, repeatPassword)) {
                session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                        UPDATE_PASSWORD_SUCCESS_KEY);
                return REDIRECT_TO_PROFILE;
            }
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
        }
        session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                CHECK_DATA_MESSAGE_KEY);
        return REDIRECT_TO_PROFILE;
    }

    private Dispatch doGet(HttpServletRequest request) {
        HttpSession session = request.getSession();
        replaceAttributeToRequest(session, request,
                CommonAttributes.ERROR_MESSAGE_KEY);
        replaceAttributeToRequest(session, request,
                CommonAttributes.SUCCESS_MESSAGE_KEY);
        return SUCCESS_GET;
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
