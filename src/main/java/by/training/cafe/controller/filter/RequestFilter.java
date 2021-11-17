package by.training.cafe.controller.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            filterChain.doFilter(request, response);
        }
    }
}
