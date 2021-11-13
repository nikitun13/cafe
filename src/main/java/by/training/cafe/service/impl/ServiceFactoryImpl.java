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
        repository = new HashMap<>();
        repository.put(DishService.class,
                new DishServiceImpl(transactionFactory));

        repository.put(UserService.class,
                new UserServiceImpl(transactionFactory, encoderService));

        repository.put(OrderService.class,
                new OrderServiceImpl(transactionFactory));

        repository.put(OrderedDishService.class,
                new OrderedDishServiceImpl(transactionFactory));

        repository.put(CommentService.class,
                new CommentServiceImpl(transactionFactory));

        repository.put(EncoderService.class, encoderService);
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
