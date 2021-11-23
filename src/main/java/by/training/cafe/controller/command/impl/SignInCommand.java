package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.*;
import by.training.cafe.controller.command.CommandUri;
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
import java.util.Optional;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

/**
 * The class {@code SignInCommand} is a class that
 * implements {@link Command}.<br/>
 * This command authorizes the user.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class SignInCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(SignInCommand.class);
    private static final Dispatch ERROR_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch SUCCESS_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("sign-in"));
    private static final Dispatch SIGNED_IN_USER_REDIRECT = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.MAIN);
    private static final Dispatch SUCCESS_POST_NO_REFERER = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.MAIN);
    private static final Dispatch ERROR_POST = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.SIGN_IN);
    private static final Dispatch INTERNAL_ERROR_POST = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final String INCORRECT_EMAIL_OR_PASSWORD_KEY
            = "signin.error.incorrect";
    private static final String EMAIL_OR_PASSWORD_IS_INVALID_MESSAGE
            = "Email or password is invalid";
    private static final String REFERER_HEADER = "referer";

    private final ServiceFactory serviceFactory;

    public SignInCommand(ServiceFactory serviceFactory) {
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
        }
        return ERROR_GET;
    }

    private Dispatch doPost(HttpServletRequest request) {
        Optional<UserDto> maybeUser;
        HttpSession session = request.getSession();
        String email = request.getParameter(CommonAttributes.EMAIL);
        String password = request.getParameter(CommonAttributes.PASSWORD);
        try {
            UserService service = serviceFactory.getService(UserService.class);
            maybeUser = service.signIn(email, password);
        } catch (ServiceException e) {
            log.error("ServiceException occurred", e);
            if (e.getMessage()
                    .startsWith(EMAIL_OR_PASSWORD_IS_INVALID_MESSAGE)) {
                session.setAttribute(CommonAttributes.EMAIL, email);
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        INCORRECT_EMAIL_OR_PASSWORD_KEY);
                return ERROR_POST;
            }
            session.setAttribute(CommonAttributes.IS_ERROR_OCCURRED,
                    Boolean.TRUE);
            session.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_INTERNAL_ERROR);
            return INTERNAL_ERROR_POST;
        }
        if (maybeUser.isPresent()) {
            UserDto user = maybeUser.get();
            session.setAttribute(CommonAttributes.USER, user);
            String lastPage
                    = (String) session.getAttribute(CommonAttributes.LAST_PAGE);
            if (lastPage == null) {
                return SUCCESS_POST_NO_REFERER;
            } else {
                session.removeAttribute(CommonAttributes.LAST_PAGE);
                return new Dispatch(DispatchType.REDIRECT, lastPage);
            }
        } else {
            session.setAttribute(CommonAttributes.EMAIL, email);
            session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                    INCORRECT_EMAIL_OR_PASSWORD_KEY);
            return ERROR_POST;
        }
    }

    private Dispatch doGet(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object user = session.getAttribute(CommonAttributes.USER);
        if (user != null) {
            return SIGNED_IN_USER_REDIRECT;
        }
        String referer = request.getHeader(REFERER_HEADER);
        if (referer != null
                && !referer.contains(CommandUri.SIGN_IN)
                && !referer.contains(CommandUri.SIGN_UP)) {
            log.debug("header referer = {}", referer);
            session.setAttribute(CommonAttributes.LAST_PAGE, referer);
        }
        replaceAttributeToRequest(session, request,
                CommonAttributes.ERROR_MESSAGE_KEY);
        replaceAttributeToRequest(session, request,
                CommonAttributes.EMAIL);
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
