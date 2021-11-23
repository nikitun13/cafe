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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static by.training.cafe.controller.command.Dispatch.DispatchType;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * The class {@code UpdateUserCommand} is a class that
 * implements {@link Command}.<br/>
 * Provides user data update functionality.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class UpdateUserCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(UpdateUserCommand.class);
    private static final Dispatch ERROR = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final Dispatch REDIRECT_TO_PROFILE = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.PROFILE);
    private static final String EMAIL_ALREADY_EXISTS_MESSAGE_KEY
            = "signup.error.emailExists";
    private static final String PHONE_ALREADY_EXISTS_MESSAGE_KEY
            = "signup.error.phoneExists";
    private static final String CHECK_DATA_MESSAGE_KEY = "cafe.error.checkData";
    private static final String SUCCESS_MESSAGE_KEY
            = "profile.updateData.success";

    private final ServiceFactory serviceFactory;

    public UpdateUserCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        if (!method.equals(HttpMethod.POST.name())) {
            response.setStatus(HTTP_BAD_METHOD);
            request.setAttribute(CommonAttributes.ERROR_STATUS, HTTP_BAD_METHOD);
            return ERROR;
        }
        HttpSession session = request.getSession();
        UserDto user = (UserDto) session.getAttribute(CommonAttributes.USER);

        if (user == null) {
            log.error("User is null");
            response.setStatus(HTTP_BAD_REQUEST);
            request.setAttribute(CommonAttributes.ERROR_STATUS,
                    HTTP_BAD_REQUEST);
            return ERROR;
        }

        String firstName = request.getParameter(CommonAttributes.FIRST_NAME);
        String lastName = request.getParameter(CommonAttributes.LAST_NAME);
        String email = request.getParameter(CommonAttributes.EMAIL);
        String phone = request.getParameter(CommonAttributes.PHONE);

        UserService userService = serviceFactory.getService(UserService.class);
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .isBlocked(user.isBlocked())
                .points(user.getPoints())
                .role(user.getRole())
                .build();
        try {
            userService.update(userDto);
            session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                    SUCCESS_MESSAGE_KEY);
            userService.findById(user.getId())
                    .ifPresent(updatedUser -> session.setAttribute(
                            CommonAttributes.USER, updatedUser));
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
            String message = e.getMessage();
            if (message.startsWith(CommonAttributes.EMAIL)) {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        EMAIL_ALREADY_EXISTS_MESSAGE_KEY);
            } else if (message.startsWith(CommonAttributes.PHONE)) {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        PHONE_ALREADY_EXISTS_MESSAGE_KEY);
            } else {
                session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        CHECK_DATA_MESSAGE_KEY);
            }
        }
        return REDIRECT_TO_PROFILE;
    }
}
