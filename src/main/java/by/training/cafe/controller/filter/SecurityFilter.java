package by.training.cafe.controller.filter;

import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.UserRole;
import by.training.cafe.util.JspPathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.HttpURLConnection;

public class SecurityFilter implements Filter {

    private static final Logger log
            = LogManager.getLogger(SecurityFilter.class);
    private static final String NOTHING_FOUND_KEY
            = "main.nothingFound";
    public static final String ERROR_PAGE
            = JspPathUtil.getPath("error");
    private static final String FORBIDDEN_MESSAGE_KEY = "cafe.forbidden";

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI().toLowerCase();
        HttpSession session = request.getSession();
        UserDto userDto = (UserDto) session.getAttribute(CommonAttributes.USER);

        if (userDto == null) {
            unauthorizedUserRequest(request, response, requestUri, filterChain);
        } else if (Boolean.TRUE.equals(userDto.isBlocked())) {
            blockedUserRequest(request, response, requestUri, filterChain);
        } else if (userDto.getRole().equals(UserRole.CLIENT)) {
            clientRequest(request, response, requestUri, filterChain);
        } else if (userDto.getRole().equals(UserRole.ADMIN)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            log.warn("Reached unexpected block");
        }
    }

    private void unauthorizedUserRequest(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String requestUri,
                                         FilterChain filterChain)
            throws IOException, ServletException {
        if (requestUri.startsWith(CommandUri.PROFILE)) {
            response.sendRedirect(CommandUri.SIGN_IN);
        } else if (requestUri.startsWith(CommandUri.ADMIN)) {
            forwardToNotFound(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void blockedUserRequest(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String requestUri,
                                    FilterChain filterChain)
            throws IOException, ServletException {
        if (requestUri.startsWith(CommandUri.PROFILE)) {
            request.setAttribute(CommonAttributes.ERROR_STATUS,
                    HttpURLConnection.HTTP_FORBIDDEN);
            request.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                    FORBIDDEN_MESSAGE_KEY);
            response.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
            request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
        } else if (requestUri.startsWith(CommandUri.ADMIN)) {
            forwardToNotFound(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void clientRequest(HttpServletRequest request,
                               HttpServletResponse response,
                               String requestUri,
                               FilterChain filterChain)
            throws ServletException, IOException {
        if (requestUri.startsWith(CommandUri.ADMIN)) {
            forwardToNotFound(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void forwardToNotFound(HttpServletRequest request,
                                   HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute(CommonAttributes.ERROR_STATUS,
                HttpURLConnection.HTTP_NOT_FOUND);
        request.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                NOTHING_FOUND_KEY);
        response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
    }
}
