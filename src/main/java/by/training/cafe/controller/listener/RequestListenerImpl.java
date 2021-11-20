package by.training.cafe.controller.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

@WebListener
public class RequestListenerImpl implements ServletRequestListener {

    private static final Logger log
            = LogManager.getLogger(RequestListenerImpl.class);

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        if (!requestURI.startsWith("/img/")
                && !requestURI.startsWith("/css/")
                && !requestURI.startsWith("/js/")) {
            String sessionId = request.getSession().getId();
            log.debug("Session id = {}. Requested URI: {} method: {}",
                    sessionId, requestURI, method);
        }
    }
}
