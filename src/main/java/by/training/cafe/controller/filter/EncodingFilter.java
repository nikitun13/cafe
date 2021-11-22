package by.training.cafe.controller.filter;

import by.training.cafe.controller.command.CommonAttributes;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    private String encoding;

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();
        encoding = (String) context.getAttribute(CommonAttributes.ENCODING);
        context.removeAttribute(CommonAttributes.ENCODING);
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
