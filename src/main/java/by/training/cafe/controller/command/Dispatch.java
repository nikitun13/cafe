package by.training.cafe.controller.command;

import java.util.Objects;

/**
 * The class {@code Dispatch} is a class that
 * represents the result of a {@link Command} execution.
 *
 * @author Nikita Romanov
 * @see Command
 */
public class Dispatch {

    private DispatchType type;
    private String path;

    public Dispatch() {
    }

    public Dispatch(DispatchType type, String path) {
        this.type = type;
        this.path = path;
    }

    public DispatchType getType() {
        return this.type;
    }

    public String getPath() {
        return this.path;
    }

    public void setType(DispatchType type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dispatch dispatch = (Dispatch) o;
        return type == dispatch.type && Objects.equals(path, dispatch.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, path);
    }

    @Override
    public String toString() {
        return "Dispatch{"
                + "type=" + type
                + ", path='" + path + '\''
                + '}';
    }

    public enum DispatchType {
        FORWARD, REDIRECT
    }
}
