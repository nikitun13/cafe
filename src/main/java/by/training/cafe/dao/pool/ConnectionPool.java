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
    private static final String DRIVER_KEY = "db.driver";

    private final BlockingQueue<Connection> pool;
    private final List<Connection> sourceConnections;

    public static ConnectionPool getInstance() {
        return INSTANCE;
    }

    private ConnectionPool() {
        log.info("Initializing connection pool");
        try {
            Class.forName(PropertiesUtil.get(DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            log.fatal("Driver not found", e);
            throw new ConnectionPoolException("Driver not found", e);
        }
        int initSize = Integer.parseInt(PropertiesUtil.get(POOL_SIZE_KEY));
        pool = new LinkedBlockingQueue<>(initSize);
        sourceConnections = new ArrayList<>(initSize);
        for (int i = 0; i < initSize; i++) {
            Connection connection = openConnection();
            sourceConnections.add(connection);
            Connection proxyConnection = proxyConnection(connection);
            pool.add(proxyConnection);
        }
        log.info("Connection pool initialized");
    }

    /**
     * Provides connection to database.
     *
     * @return {@link Connection} to database.
     */
    public Connection getConnection() {
        try {
            Connection connection = pool.take();
            log.debug("Give connection. Remaining connections: {}", pool.size());
            return connection;
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
        log.info("Closing connection pool");
        for (Connection sourceConnection : sourceConnections) {
            try {
                sourceConnection.close();
            } catch (SQLException e) {
                log.fatal(e);
            }
        }
        log.info("Connection pool closed");
    }

    private Connection proxyConnection(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                ConnectionPool.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> method.getName().equals("close")
                        ? putBackConnection((Connection) proxy)
                        : method.invoke(connection, args));
    }

    private boolean putBackConnection(Connection connection) {
        boolean result = pool.add(connection);
        log.debug("Put connection back. Remaining connections: {}", pool.size());
        return result;
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
