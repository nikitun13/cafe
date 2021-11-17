package by.training.cafe.controller.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Describes the interface of a command that
 * executes a task depending on the requested URL.
 *
 * @author Nikita Romanov
 */
public interface Command {

    /**
     * Executes a task.
     *
     * @param request  request to be processed.
     * @param response response to the given {@code request}.
     * @return {@link Dispatch} to dispatch result of execution.
     * @see Dispatch
     */
    Dispatch execute(HttpServletRequest request, HttpServletResponse response);
}
