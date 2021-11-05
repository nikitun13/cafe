package by.training.cafe.dao.pool;

import by.training.cafe.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The class {@code ConnectionPool} is a singleton that
 * manages {@code Connections} to database.<br/>
 * This class is threadsafe.
 *
 * @author Nikita Romanov
 * @see Connection
 */
public final class ConnectionPool {

    private static final Logger log = LogManager.getLogger(ConnectionPool.class);

    private static final ConnectionPool INSTANCE = new ConnectionPool();

    private static final String PASSWORD_KEY = "db.password";
    private static final String USERNAME_KEY = "db.username";
    private static final String URL_KEY = "db.url";
    private static final String POOL_SIZE_KEY = "db.pool.size";

    private final BlockingQueue<Connection> pool;
    private final List<Connection> sourceConnections;

    public static ConnectionPool getInstance() {
        return INSTANCE;
    }

    private ConnectionPool() {
        int initSize = Integer.parseInt(PropertiesUtil.get(POOL_SIZE_KEY));
        pool = new LinkedBlockingQueue<>(initSize);
        sourceConnections = new ArrayList<>(initSize);
        for (int i = 0; i < initSize; i++) {
            Connection connection = openConnection();
            Connection proxyConnection = proxyConnection(connection);
            sourceConnections.add(connection);
            pool.add(proxyConnection);
        }
    }

    /**
     * Provides connection to database.
     *
     * @return {@link Connection} to database.
     */
    public Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            log.fatal(e);
            Thread.currentThread().interrupt();
            throw new ConnectionPoolException(e);
        }
    }

    /**
     * Closes all connections.
     */
    public void closePool() {
        try {
            for (Connection sourceConnection : sourceConnections) {
                sourceConnection.close();
            }
        } catch (SQLException e) {
            log.fatal(e);
        }
    }

    private Connection proxyConnection(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                ConnectionPool.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> method.getName().equals("close")
                        ? pool.add((Connection) proxy)
                        : method.invoke(connection, args));
    }

    private Connection openConnection() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            log.fatal(e);
            throw new ConnectionPoolException(e);
        }
    }

    private static class ConnectionPoolException extends RuntimeException {

        ConnectionPoolException() {
        }

        ConnectionPoolException(String message) {
            super(message);
        }

        ConnectionPoolException(String message, Throwable cause) {
            super(message, cause);
        }

        ConnectionPoolException(Throwable cause) {
            super(cause);
        }
    }
}