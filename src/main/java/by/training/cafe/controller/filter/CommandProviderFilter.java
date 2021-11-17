package by.training.cafe.controller.filter;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandProvider;
import by.training.cafe.service.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CommandProviderFilter implements Filter {

    private static final Logger log
            = LogManager.getLogger(CommandProviderFilter.class);

    private CommandProvider commandProvider;

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();
        String serviceFactoryKey = "serviceFactory";
        ServiceFactory serviceFactory
                = (ServiceFactory) context.getAttribute(serviceFactoryKey);
        context.removeAttribute(serviceFactoryKey);
        commandProvider = new CommandProvider(serviceFactory);
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String commandKey = request.getRequestURI().toLowerCase();
        Command command = commandProvider.getCommand(commandKey);
        String sessionId = request.getSession().getId();
        log.debug("command: {} for sessionId = {}", command, sessionId);
        request.setAttribute("command", command);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
