package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.Dispatch.DispatchType;
import by.training.cafe.controller.command.HttpMethod;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.UserRole;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.ServiceFactory;
import by.training.cafe.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

/**
 * The class {@code AdminUpdateUserCommand} is a class that
 * implements {@link Command}.<br/>
 * Updates user's role, points and blocked status.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class AdminUpdateUserCommand implements Command {

    private static final Logger log
            = LogManager.getLogger(AdminUpdateUserCommand.class);
    private static final Dispatch BAD_METHOD_ERROR = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ERROR);
    private static final Dispatch REDIRECT_TO_ADMIN_USERS = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.ADMIN_USERS);
    private static final String CHECK_INPUT_MESSAGE_KEY = "cafe.error.checkData";
    private static final String UPDATED_MESSAGE_KEY = "admin.users.success";
    private static final String USER_ID = "userId";
    private static final String POINTS = "points";
    private static final String ROLE = "role";
    private static final String IS_BLOCKED = "isBlocked";

    private final ServiceFactory serviceFactory;

    public AdminUpdateUserCommand(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String method = request.getMethod();
        HttpSession session = request.getSession();
        if (!method.equals(HttpMethod.POST.name())) {
            session.setAttribute(CommonAttributes.IS_ERROR_OCCURRED, Boolean.TRUE);
            session.setAttribute(CommonAttributes.ERROR_STATUS, HTTP_BAD_METHOD);
            return BAD_METHOD_ERROR;
        }
        try {
            Long userId = Long.valueOf(request.getParameter(USER_ID));
            Long points = Long.valueOf(request.getParameter(POINTS));
            UserRole role = UserRole.valueOf(request.getParameter(ROLE));
            Boolean isBlocked = Boolean.valueOf(request.getParameter(IS_BLOCKED));

            UserService userService
                    = serviceFactory.getService(UserService.class);
            Optional<UserDto> maybeUser = userService.findById(userId);

            if (maybeUser.isPresent()) {
                UserDto user = maybeUser.get();
                user.setPoints(points);
                user.setRole(role);
                user.setBlocked(isBlocked);

                userService.update(user);

                session.setAttribute(CommonAttributes.SUCCESS_MESSAGE_KEY,
                        UPDATED_MESSAGE_KEY);
                return REDIRECT_TO_ADMIN_USERS;
            }
            log.error("User is empty");
        } catch (IllegalArgumentException | ServiceException e) {
            log.error("Exception occurred", e);
        }
        session.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                CHECK_INPUT_MESSAGE_KEY);
        return REDIRECT_TO_ADMIN_USERS;
    }
}
