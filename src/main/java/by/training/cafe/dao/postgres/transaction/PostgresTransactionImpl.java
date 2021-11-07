package by.training.cafe.dao.postgres.transaction;

import by.training.cafe.dao.*;
import by.training.cafe.dao.postgres.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The class {@code PostgresTransactionImpl} is a class that
 * implements {@link Transaction}.<br/>
 * Creates PostgreSQL DAOs.
 *
 * @author Nikita Romanov
 * @see Transaction
 */
public class PostgresTransactionImpl implements Transaction {

    private static final Logger log
            = LogManager.getLogger(PostgresTransactionImpl.class);


    private static final Map<
            Class<? extends BaseDao<?, ?>>,
            Function<Connection, ? extends BaseDao<?, ?>>> MAPPER;

    static {
        MAPPER = new HashMap<>();
        MAPPER.put(DishDao.class, DishDaoImpl::new);
        MAPPER.put(OrderDao.class, OrderDaoImpl::new);
        MAPPER.put(CommentDao.class, CommentDaoImpl::new);
        MAPPER.put(OrderedDishDao.class, OrderedDishDaoImpl::new);
        MAPPER.put(UserDao.class, UserDaoImpl::new);
    }

    private final Connection connection;

    public PostgresTransactionImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseDao<?, ?>> T createDao(Class<T> daoClass) {
        log.debug("received class: {}", daoClass);
        if (MAPPER.containsKey(daoClass)) {
            T daoImpl = (T) MAPPER.get(daoClass).apply(connection);
            log.debug("Dao implementation: {}", daoImpl.getClass());
            return daoImpl;
        } else {
            log.fatal("No such dao implementation: {}", daoClass);
            throw new PostgresTransactionException(
                    "No such dao implementation for: " + daoClass);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.fatal("Unexpected exception", e);
        }
    }

    private static class PostgresTransactionException extends RuntimeException {

        PostgresTransactionException() {
        }

        PostgresTransactionException(String message) {
            super(message);
        }

        PostgresTransactionException(String message, Throwable cause) {
            super(message, cause);
        }

        PostgresTransactionException(Throwable cause) {
            super(cause);
        }
    }
}
