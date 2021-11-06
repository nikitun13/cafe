package by.training.cafe.dao;

/**
 * The class {@code Transaction} is a class that
 * creates DAO implementations during a single store connection.<br/>
 * Should be closed at the end of work.
 *
 * @author Nikita Romanov
 * @see AutoCloseable
 */
public interface Transaction extends AutoCloseable {

    /**
     * Creates implementations of the given {@code daoClass}.
     *
     * @param daoClass class which implementation needs to be created.
     * @param <T>      type of this {@code daoClass}.
     * @return implementation of the given {@code daoClass}.
     */
    <T extends BaseDao<?, ?>> T createDao(Class<T> daoClass);
}
