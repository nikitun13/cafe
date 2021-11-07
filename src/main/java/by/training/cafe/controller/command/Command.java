package by.training.cafe.controller.command;

import javax.servlet.http.HttpServletRequest;

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
     * @param request request to be processed.
     * @return {@link Dispatch} to dispatch result of execution.
     * @see Dispatch
     */
    Dispatch execute(HttpServletRequest request);
}
