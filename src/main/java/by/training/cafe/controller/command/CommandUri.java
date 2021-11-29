package by.training.cafe.controller.command;

/**
 * The class {@code CommandUri} is a utility class
 * that provides all available URLs for commands.
 *
 * @author Nikita Romanov
 * @see CommandProvider
 */
public final class CommandUri {

    // public URI
    public static final String MAIN = "/";
    public static final String SIGN_IN = "/signin";
    public static final String LOCALE = "/locale";
    public static final String SIGN_UP = "/signup";
    public static final String SIGN_OUT = "/signout";
    public static final String DISH_PAGE = "/dish";
    public static final String ERROR = "/error";
    public static final String ASYNC_ORDERED_DISHES = "/async/ordereddishes";

    // non blocked users URI
    public static final String PROFILE = "/profile";
    public static final String CART = "/profile/cart";
    public static final String CREATE_COMMENT = "/profile/createcomment";
    public static final String ORDERS = "/profile/orders";
    public static final String UPDATE_USER = "/profile/updateuser";

    // admin URI
    public static final String ADMIN = "/admin";
    public static final String ADMIN_ORDERS = "/admin/orders";
    public static final String ADMIN_COMPLETE_ORDERS = "/admin/completeorders";
    public static final String ADMIN_TO_PENDING_ORDERS = "/admin/pendingorders";
    public static final String ADMIN_TO_NOT_COLLECTED_ORDERS = "/admin/notcollectedorders";
    public static final String ADMIN_CANCEL_ORDERS = "/admin/cancelorders";
    public static final String ADMIN_USERS = "/admin/users";
    public static final String ADMIN_UPDATE_USER = "/admin/updateuser";
    public static final String ADMIN_DISHES = "/admin/dishes";
    public static final String ADMIN_CREATE_DISH = "/admin/createdish";
    public static final String ADMIN_UPDATE_DISH = "/admin/updatedish";
    public static final String ADMIN_UPDATE_DESCRIPTION = "/admin/updatedescription";

    private CommandUri() {
    }
}
