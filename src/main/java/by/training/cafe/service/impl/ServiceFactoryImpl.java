package by.training.cafe.service.impl;

import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dao.postgres.transaction.TransactionFactoryImpl;
import by.training.cafe.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The class {@code ServiceFactoryImpl} is a class that
 * implements {@link ServiceFactory} and {@link AutoCloseable}.<br/>
 * Should be closed in the end of work.
 *
 * @author Nikita Romanov
 * @see ServiceFactory
 * @see AutoCloseable
 */
public final class ServiceFactoryImpl implements ServiceFactory, AutoCloseable {

    private static final Logger log
            = LogManager.getLogger(ServiceFactoryImpl.class);
    private static final ServiceFactoryImpl INSTANCE
            = new ServiceFactoryImpl();

    private final Map<Class<? extends Service>, Service> repository;

    private ServiceFactoryImpl() {
        TransactionFactory transactionFactory
                = TransactionFactoryImpl.getInstance();
        EncoderService encoderService = new BCryptEncoderServiceImpl();
        UserService userService = new UserServiceImpl(
                transactionFactory, encoderService);
        OrderService orderService = new OrderServiceImpl(transactionFactory);
        OrderedDishService orderedDishService = new OrderedDishServiceImpl(
                transactionFactory);
        OrderProcessService orderProcessService = new OrderProcessServiceImpl(
                orderService, orderedDishService, userService);
        repository = new HashMap<>();
        repository.put(DishService.class,
                new DishServiceImpl(transactionFactory));

        repository.put(UserService.class, userService);
        repository.put(OrderService.class, orderService);
        repository.put(OrderedDishService.class, orderedDishService);
        repository.put(CommentService.class,
                new CommentServiceImpl(transactionFactory));
        repository.put(EncoderService.class, encoderService);
        repository.put(OrderProcessService.class, orderProcessService);
    }

    public static ServiceFactoryImpl getInstance() {
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Service> T getService(Class<T> serviceClass) {
        log.debug("received class: {}", serviceClass);
        if (repository.containsKey(serviceClass)) {
            T serviceImpl = (T) repository.get(serviceClass);
            log.debug("Service implementation: {}", serviceImpl.getClass());
            return serviceImpl;
        } else {
            log.fatal("No such service implementation for: {}", serviceClass);
            throw new ServiceFactoryException(
                    "No such service implementation for: " + serviceClass);
        }
    }

    @Override
    public void close() {
        TransactionFactoryImpl.getInstance().close();
    }

    private static class ServiceFactoryException extends RuntimeException {

        ServiceFactoryException() {
        }

        ServiceFactoryException(String message) {
            super(message);
        }

        ServiceFactoryException(String message, Throwable cause) {
            super(message, cause);
        }

        ServiceFactoryException(Throwable cause) {
            super(cause);
        }
    }
}
