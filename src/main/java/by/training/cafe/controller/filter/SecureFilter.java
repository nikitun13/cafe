package by.training.cafe.controller.filter;

import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.UserRole;
import by.training.cafe.util.JspPathUtil;

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

public class SecureFilter implements Filter {

    private static final String NOTHING_FOUND_KEY
            = "main.nothingFound";
    public static final String ERROR_PAGE = JspPathUtil.getPath("error");

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestUri = request.getRequestURI().toLowerCase();
        HttpSession session = request.getSession();
        UserDto userDto = (UserDto) session.getAttribute(CommonAttributes.USER);
        if (requestUri.startsWith(CommandUri.ADMIN)) {
            if (userDto != null && userDto.getRole() == UserRole.ADMIN) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                request.setAttribute(CommonAttributes.ERROR_STATUS,
                        HttpURLConnection.HTTP_NOT_FOUND);
                request.setAttribute(CommonAttributes.ERROR_MESSAGE_KEY,
                        NOTHING_FOUND_KEY);
                request.getRequestDispatcher(ERROR_PAGE)
                        .forward(servletRequest, servletResponse);
            }
        } else if (requestUri.startsWith(CommandUri.CART)
                || requestUri.startsWith(CommandUri.PROFILE)) {
            if (userDto != null) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                ((HttpServletResponse) servletResponse)
                        .sendRedirect(CommandUri.SIGN_IN);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}

