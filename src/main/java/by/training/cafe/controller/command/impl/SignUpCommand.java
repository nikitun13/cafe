package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.CreateUserDto;
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
 * The class {@code SignUpCommand} is a class that
 * implements {@link Command}.<br/>
 * Registers new users.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class SignUpCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(SignUpCommand.class);

    private static final Dispatch ERROR_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("error"));
    private static final Dispatch SUCCESS_GET = new Dispatch(
            DispatchType.FORWARD,
            JspPathUtil.getPath("sign-up"));
    private static final Dispatch SIGNED_IN_USER_REDIRECT = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.MAIN);
    private static final Dispatch SUCCESS_POST = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.SIGN_IN);
    private static final Dispatch ERROR_POST = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.SIGN_UP);
    private static final String REFERER_HEADER = "referer";
    private static final String SIGN_UP_SUCCESS_MESSAGE = "signup.success";
    private static final String CHECK_INPUT_DATA_MESSAGE_KEY
            = "cafe.error.checkData";
    private static final String EMAIL_ALREADY_EXISTS_MESSAGE_KEY
            = "signup.error.emailExists";
    private static final String PHONE_ALREADY_EXISTS_MESSAGE_KEY
            = "signup.error.phoneExists";

    private final ServiceFactory serviceFactory;

    public SignUpCommand(ServiceFactory serviceFactory) {
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
            return methodNotAllowed(request, response);
        }
    }

    private Dispatch doPost(HttpServletRequest request) {
        String firstName = request.getParameter(CommonAttributes.FIRST_NAME);
        String lastName = request.getParameter(CommonAttributes.LAST_NAME);
        String email = request.getParameter(CommonAttributes.EMAIL);
        String password = request.getParameter(CommonAttributes.PASSWORD);
        String repeatPassword = request.getParameter(
                CommonAttributes.REPEAT_PASSWORD);
        String phone = request.getParameter(CommonAttributes.PHONE);

        CreateUserDto createUserDto = CreateUserDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .repeatPassword(repeatPassword)
                .phone(phone)
                .build();

        HttpSession session = request.getSession();
        UserService userService = serviceFactory.getService(UserService.class);
        try {
            userService.signUp(createUserDto);
            session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                    SIGN_UP_SUCCESS_MESSAGE);
            return SUCCESS_POST;
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
            setUserParametersToSession(createUserDto, session);
            String message = e.getMessage();
            if (message.startsWith(CommonAttributes.EMAIL)) {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        EMAIL_ALREADY_EXISTS_MESSAGE_KEY);
            } else if (message.startsWith(CommonAttributes.PHONE)) {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        PHONE_ALREADY_EXISTS_MESSAGE_KEY);
            } else {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        CHECK_INPUT_DATA_MESSAGE_KEY);
            }
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
                CommonAttributes.FIRST_NAME);
        replaceAttributeToRequest(session, request,
                CommonAttributes.LAST_NAME);
        replaceAttributeToRequest(session, request,
                CommonAttributes.EMAIL);
        replaceAttributeToRequest(session, request,
                CommonAttributes.PHONE);
        return SUCCESS_GET;
    }

    private void setUserParametersToSession(CreateUserDto dto,
                                            HttpSession session) {
        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        String email = dto.getEmail();
        String phone = dto.getPhone();

        session.setAttribute(CommonAttributes.FIRST_NAME, firstName);
        session.setAttribute(CommonAttributes.LAST_NAME, lastName);
        session.setAttribute(CommonAttributes.EMAIL, email);
        session.setAttribute(CommonAttributes.PHONE, phone);
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

    private Dispatch methodNotAllowed(HttpServletRequest request,
                                      HttpServletResponse response) {
        response.setStatus(HTTP_BAD_METHOD);
        request.setAttribute(CommonAttributes.ERROR_STATUS, HTTP_BAD_METHOD);
        return ERROR_GET;
    }
}
