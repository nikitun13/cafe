package by.training.cafe.controller.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener
public class SessionListenerImpl implements HttpSessionAttributeListener {

    private static final Logger log
            = LogManager.getLogger(SessionListenerImpl.class);
    public static final String JSTL_REQUEST_CHARSET_KEY
            = "javax.servlet.jsp.jstl.fmt.request.charset";

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        String sessionId = event.getSession().getId();
        String name = event.getName();
        Object value = event.getValue();

        log.debug("Attribute '{}' with value = '{}'"
                        + " was added to session with id = {}",
                name, value, sessionId);
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        String sessionId = event.getSession().getId();
        String name = event.getName();
        Object value = event.getValue();

        log.debug("Attribute '{}' with value = '{}'"
                        + " was removed from session with id = {}",
                name, value, sessionId);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        String name = event.getName();
        if (!name.equals(JSTL_REQUEST_CHARSET_KEY)) {
            String sessionId = event.getSession().getId();
            Object value = event.getValue();

            log.debug("Attribute '{}' was replaced with value = '{}'."
                            + " Session id = {}",
                    name, value, sessionId);
        }
    }
}
