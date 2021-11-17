package by.training.cafe.controller.filter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class RequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.length() > 1 && requestURI.endsWith("/")) {
            httpResponse.sendRedirect(
                    requestURI.substring(0, requestURI.length() - 1));
        } else if (requestURI.startsWith("/img/")
                || requestURI.startsWith("/css/")
                || requestURI.startsWith("/js/")) {
            httpRequest.getRequestDispatcher(requestURI)
                    .forward(request, response);
        } else {
            String localeKey = "locale";
            HttpSession session = httpRequest.getSession();
            if (session.getAttribute(localeKey) == null) {
                Optional<Cookie> maybeLocaleCookie
                        = Arrays.stream(httpRequest.getCookies())
                        .filter(cookie -> cookie.getName().equals(localeKey))
                        .findFirst();
                maybeLocaleCookie.ifPresent(cookie ->
                        session.setAttribute(localeKey, cookie.getValue()));
            }
            filterChain.doFilter(request, response);
        }
    }
}
