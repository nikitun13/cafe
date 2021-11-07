package by.training.cafe.service;

/**
 * The class {@code ServiceFactory} is a class that
 * provides services implementations.
 *
 * @author Nikita Romanov
 */
public interface ServiceFactory {

    /**
     * Returns implementations of the given {@code serviceClass}.
     *
     * @param serviceClass class which implementation needs to be returned.
     * @param <T>          type of this {@code serviceClass}.
     * @return implementation of the given {@code serviceClass}.
     */
    <T extends Service> T getService(Class<T> serviceClass);
}
