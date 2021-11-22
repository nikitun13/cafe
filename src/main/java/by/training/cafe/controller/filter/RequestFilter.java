package by.training.cafe.controller.filter;

import by.training.cafe.controller.command.CommonAttributes;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class RequestFilter implements Filter {

    private static final String IMG_URI = "/img/";
    private static final String CSS_URI = "/css/";
    private static final String JS_URI = "/js/";
    private static final String SLASH = "/";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.length() > 1 && requestURI.endsWith(SLASH)) {
            httpResponse.sendRedirect(
                    requestURI.substring(0, requestURI.length() - 1));
        } else if (requestURI.startsWith(IMG_URI)
                || requestURI.startsWith(CSS_URI)
                || requestURI.startsWith(JS_URI)) {
            httpRequest.getRequestDispatcher(requestURI)
                    .forward(request, response);
        } else {
            HttpSession session = httpRequest.getSession();
            if (session.getAttribute(CommonAttributes.LOCALE) == null) {
                Cookie[] cookies = httpRequest.getCookies();
                if (cookies != null) {
                    Optional<Cookie> maybeLocaleCookie
                            = Arrays.stream(cookies)
                            .filter(cookie -> cookie.getName()
                                    .equals(CommonAttributes.LOCALE))
                            .findFirst();
                    maybeLocaleCookie.ifPresent(cookie ->
                            session.setAttribute(CommonAttributes.LOCALE,
                                    cookie.getValue()));
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
