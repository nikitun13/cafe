package by.training.cafe.controller.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    private String encoding;

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();
        String encodingKey = "encoding";
        encoding = (String) context.getAttribute(encodingKey);
        context.removeAttribute(encodingKey);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);

        filterChain.doFilter(request, response);
    }
}
