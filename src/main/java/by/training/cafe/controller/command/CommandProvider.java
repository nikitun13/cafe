package by.training.cafe.controller.command;

import by.training.cafe.controller.command.impl.*;
import by.training.cafe.service.ServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The class {@code CommandProvider} is a class
 * that provides implementations of the {@link Command}.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class CommandProvider {

    private final Map<String, Command> repository;
    private final Command unknownCommand;

    public CommandProvider(ServiceFactory serviceFactory) {
        unknownCommand = new UnknownCommand();
        repository = new HashMap<>();
        repository.put(CommandUri.MAIN, new MainCommand(serviceFactory));
        repository.put(CommandUri.LOCALE, new LocaleCommand());
        repository.put(CommandUri.ERROR, new ErrorCommand());
        repository.put(CommandUri.SIGN_IN, new SignInCommand(serviceFactory));
        repository.put(CommandUri.SIGN_UP, new SignUpCommand(serviceFactory));
        repository.put(CommandUri.SIGN_OUT, new SignOutCommand());
        repository.put(CommandUri.DISH_PAGE, new DishPageCommand(serviceFactory));
        repository.put(CommandUri.CART, new CartCommand(serviceFactory));
        repository.put(CommandUri.PROFILE, new ProfileCommand(serviceFactory));
        repository.put(CommandUri.UPDATE_USER, new UpdateUserCommand(serviceFactory));
        repository.put(CommandUri.ORDERS, new OrdersCommand(serviceFactory));
        repository.put(CommandUri.ASYNC_ORDERED_DISHES, new OrderedDishesAsyncCommand(serviceFactory));
        repository.put(CommandUri.ADMIN, new AdminCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_ORDERS, new AdminOrdersCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_COMPLETE_ORDERS, new CompleteOrdersCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_CANCEL_ORDERS, new CancelOrdersCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_TO_PENDING_ORDERS, new ToPendingOrdersCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_TO_NOT_COLLECTED_ORDERS, new ToNotCollectedOrdersCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_USERS, new AdminUsersCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_UPDATE_USER, new AdminUpdateUserCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_DISHES, new AdminDishesCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_CREATE_DISH, new AdminCreateDishCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_UPDATE_DISH, new AdminUpdateDishCommand(serviceFactory));
        repository.put(CommandUri.ADMIN_UPDATE_DESCRIPTION, new AdminUpdateDishDescriptionCommand(serviceFactory));
        repository.put(CommandUri.CREATE_COMMENT, new CreateCommentCommand(serviceFactory));
    }

    public Command getCommand(String key) {
        return repository.getOrDefault(key, unknownCommand);
    }
}
