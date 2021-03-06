package by.training.cafe.controller.listener;

import by.training.cafe.controller.command.CommonAttributes;
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
    private static final String ENCODING_KEY = "app.encoding";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Context initialized");
        Locale.setDefault(Locale.ENGLISH);
        ServiceFactory serviceFactory = ServiceFactoryImpl.getInstance();
        ServletContext context = sce.getServletContext();
        context.setAttribute(CommonAttributes.SERVICE_FACTORY, serviceFactory);
        String encoding = PropertiesUtil.get(ENCODING_KEY);
        context.setAttribute(CommonAttributes.ENCODING, encoding);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Context destroyed");
        ServiceFactoryImpl.getInstance().close();
    }
}
