package by.training.cafe.dao.postgres.transaction;

import by.training.cafe.dao.Transaction;
import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dao.pool.ConnectionPool;

/**
 * The class {@code TransactionFactoryImpl} is a class that
 * implements {@link TransactionFactory}.<br/>
 * Creates {@code transactions} for PostgreSQL DAOs.
 *
 * @author Nikita Romanov
 * @see Transaction
 */
public final class TransactionFactoryImpl implements TransactionFactory {

    private static final TransactionFactoryImpl INSTANCE
            = new TransactionFactoryImpl();

    private final ConnectionPool pool;

    private TransactionFactoryImpl() {
        pool = ConnectionPool.getInstance();
    }

    public static TransactionFactoryImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Transaction createTransaction() {
        return new PostgresTransactionImpl(pool.getConnection());
    }

    @Override
    public void close() {
        pool.closePool();
    }
}
