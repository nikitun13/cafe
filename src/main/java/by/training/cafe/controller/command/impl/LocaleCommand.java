package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUrl;
import by.training.cafe.controller.command.Dispatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static by.training.cafe.controller.command.Dispatch.DispatchType;

/**
 * The class {@code LocaleCommand} is a class that
 * implements {@link Command}.<br/>
 * Changes current user locale.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class LocaleCommand implements Command {

    private static final Logger log = LogManager.getLogger(LocaleCommand.class);
    private static final List<String> LOCALES;
    private static final Dispatch REDIRECT_HOME = new Dispatch(
            DispatchType.REDIRECT,
            CommandUrl.MAIN);

    static {
        LOCALES = List.of("en-us", "ru-ru", "de-de");
    }

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String lc = request.getParameter("lc");
        log.debug("Parameter locale = {}", lc);
        if (lc != null) {
            lc = lc.toLowerCase();
            if (LOCALES.contains(lc)) {
                request.getSession().setAttribute("locale", lc);
                Cookie cookie = new Cookie("locale", lc);
                cookie.setMaxAge(Integer.MAX_VALUE);
                response.addCookie(cookie);
            }
        }
        String referer = request.getHeader("referer");
        log.debug("header referer = {}", referer);
        if (referer == null || referer.startsWith(CommandUrl.LOCALE)) {
            return REDIRECT_HOME;
        } else {
            return new Dispatch(DispatchType.REDIRECT, referer);
        }
    }
}
