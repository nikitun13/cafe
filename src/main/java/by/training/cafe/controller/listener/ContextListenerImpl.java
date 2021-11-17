package by.training.cafe.controller.listener;

import by.training.cafe.service.ServiceFactory;
import by.training.cafe.service.impl.ServiceFactoryImpl;
import by.training.cafe.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Locale;

@WebListener
public class ContextListenerImpl implements ServletContextListener {

    private static final Logger log
            = LogManager.getLogger(ContextListenerImpl.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Context initialized");
        Locale.setDefault(Locale.ENGLISH);
        ServiceFactory serviceFactory = ServiceFactoryImpl.getInstance();
        ServletContext context = sce.getServletContext();
        context.setAttribute("serviceFactory", serviceFactory);

        String encoding = PropertiesUtil.get("app.encoding");
        context.setAttribute("encoding", encoding);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Context destroyed");
        ServiceFactoryImpl.getInstance().close();
    }
}
