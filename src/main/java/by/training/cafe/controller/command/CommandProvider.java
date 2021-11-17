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
        repository.put(CommandUrl.MAIN, new MainCommand(serviceFactory));
        repository.put(CommandUrl.LOCALE, new LocaleCommand());
        repository.put(CommandUrl.SIGN_IN, new SignInCommand(serviceFactory));
        repository.put(CommandUrl.SIGN_UP, new SignUpCommand(serviceFactory));
        repository.put(CommandUrl.SIGN_OUT, new SignOutCommand());
        repository.put(CommandUrl.DISH_PAGE, new DishPageCommand(serviceFactory));
    }

    public Command getCommand(String key) {
        return repository.getOrDefault(key, unknownCommand);
    }
}
