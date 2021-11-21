package by.training.cafe.controller.command;

/**
 * The class {@code CommandUrl} is a utility class
 * that provides all available URLs for commands.
 *
 * @author Nikita Romanov
 * @see CommandProvider
 */
public final class CommandUrl {

    public static final String MAIN = "/";
    public static final String SIGN_IN = "/signin";
    public static final String LOCALE = "/locale";
    public static final String SIGN_UP = "/signup";
    public static final String SIGN_OUT = "/signout";
    public static final String DISH_PAGE = "/dish";
    public static final String ERROR = "/error";

    private CommandUrl() {
    }
}
