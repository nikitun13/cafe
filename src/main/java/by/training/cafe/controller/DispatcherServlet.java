package by.training.cafe.controller;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommonAttributes;
import by.training.cafe.controller.command.Dispatch;
import by.training.cafe.controller.command.Dispatch.DispatchType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DispatcherServlet extends HttpServlet {

    private static final Logger log
            = LogManager.getLogger(DispatcherServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) {
        Command command = (Command) req.getAttribute(CommonAttributes.COMMAND);
        log.debug("get command attribute: {}", command);
        Dispatch result = command.execute(req, resp);
        log.debug("execution result: {}", result);
        DispatchType type = result.getType();
        if (type != DispatchType.RETURN) {
            String path = result.getPath();
            switch (type) {
                case FORWARD -> forward(path, req, resp);
                case REDIRECT -> redirect(path, resp);
                default -> log.error("No such type: {}", type);
            }
        }
    }

    private void forward(String path, HttpServletRequest req,
                         HttpServletResponse resp) {
        try {
            req.getRequestDispatcher(path).forward(req, resp);
        } catch (ServletException e) {
            log.error("ServletException occurred", e);
        } catch (IOException e) {
            log.error("IOException exception occurred", e);
        }
    }

    private void redirect(String path, HttpServletResponse resp) {
        try {
            resp.sendRedirect(path);
        } catch (IOException e) {
            log.error("IOException exception occurred", e);
        }
    }
}
