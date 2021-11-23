package by.training.cafe.controller.command.impl;

import by.training.cafe.controller.command.Command;
import by.training.cafe.controller.command.CommandUri;
import by.training.cafe.controller.command.CommonAttributes;
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
    private static final List<String> LOCALES = List.of("en-us", "ru-ru", "de-de");
    private static final Dispatch REDIRECT_HOME = new Dispatch(
            DispatchType.REDIRECT,
            CommandUri.MAIN);
    private static final String LOCALE_PARAMETER_KEY = "lc";
    private static final String REFERER_HEADER = "referer";

    @Override
    public Dispatch execute(HttpServletRequest request,
                            HttpServletResponse response) {
        String lc = request.getParameter(LOCALE_PARAMETER_KEY);
        log.debug("Parameter locale = {}", lc);
        if (lc != null) {
            lc = lc.toLowerCase();
            if (LOCALES.contains(lc)) {
                request.getSession().setAttribute(CommonAttributes.LOCALE, lc);
                Cookie cookie = new Cookie(CommonAttributes.LOCALE, lc);
                cookie.setMaxAge(Integer.MAX_VALUE);
                response.addCookie(cookie);
            }
        }
        String referer = request.getHeader(REFERER_HEADER);
        log.debug("header referer = {}", referer);
        if (referer == null || referer.contains(CommandUri.LOCALE)) {
            return REDIRECT_HOME;
        } else {
            return new Dispatch(DispatchType.REDIRECT, referer);
        }
    }
}
