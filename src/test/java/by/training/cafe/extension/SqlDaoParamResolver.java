package by.training.cafe.extension;

import by.training.cafe.dao.postgres.AbstractSqlDao;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Connection;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class SqlDaoParamResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();
        return AbstractSqlDao.class.isAssignableFrom(type)
                && !Modifier.isAbstract(type.getModifiers());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();
        return extensionContext.getStore(GLOBAL).getOrComputeIfAbsent(
                type,
                it -> createInstance(it, extensionContext));
    }

    private Object createInstance(Class<?> type, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        try {
            ExtensionContext.Store store = extensionContext.getStore(GLOBAL);
            Connection connection = store.get("database-connection", Connection.class);
            return type.getDeclaredConstructor(Connection.class).newInstance(connection);
        } catch (NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new ParameterResolutionException("creating object exception", e);
        }
    }
}
