package by.training.cafe.extension;

import by.training.cafe.dao.pool.ConnectionPool;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.Connection;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.api.extension.ExtensionContext.Store;

public class DatabaseExtension implements BeforeAllCallback, Store.CloseableResource {

    private static boolean started = false;
    private Connection connection;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            started = true;
            Connection connection = ConnectionPool.getInstance().getConnection();
            Store store = context.getRoot().getStore(GLOBAL);
            store.put(DatabaseExtension.class, this);
            store.put("database-connection", connection);
        }
    }

    @Override
    public void close() {
        ConnectionPool.getInstance().closePool();
    }
}
