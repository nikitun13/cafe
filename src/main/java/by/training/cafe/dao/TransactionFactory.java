package by.training.cafe.dao;

/**
 * The class {@code TransactionFactory} is a factory
 * that creates {@code transactions}.<br/>
 * Should be closed at the end of work.
 *
 * @author Nikita Romanov
 * @see Transaction
 */
public interface TransactionFactory extends AutoCloseable {

    /**
     * Creates a {@link Transaction} to access the store.
     *
     * @return new {@link Transaction}.
     */
    Transaction createTransaction();
}
