package by.training.cafe.service;

import by.training.cafe.dto.CommentDto;
import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The class {@code CommentService} is a class that
 * extends {@link Service}.<br/>
 * Provides different business logic with {@code comment} entities
 * using {@link CommentDto}.
 *
 * @author Nikita Romanov
 * @see Service
 * @see CommentDto
 */
public interface CommentService extends Service {

    /**
     * Returns all entities mapped to {@link CommentDto}
     * from storage.
     *
     * @return all entities mapped to {@link CommentDto}.
     * @throws ServiceException if DaoException occurred.
     */
    List<CommentDto> findAll() throws ServiceException;

    /**
     * Returns entities mapped to {@link CommentDto}
     * from storage with the given {@code limit} and {@code offset}.
     *
     * @param limit  returning number of DTOs.
     * @param offset offset from storage.
     * @return all entities mapped to {@link CommentDto}.
     * @throws ServiceException if DaoException occurred or
     *                          {@code limit} or {@code offset}
     *                          is invalid.
     */
    List<CommentDto> findAll(long limit, long offset) throws ServiceException;

    /**
     * Counts number of entities in the storage.
     *
     * @return number of entities.
     * @throws ServiceException if DaoException occurred.
     */
    Long count() throws ServiceException;

    /**
     * Finds entity by {@code id} and maps it to {@link CommentDto}.
     *
     * @param id entity {@code id}.
     * @return optional {@code commentDto}. If entity was found
     * optional contains {@link CommentDto}, otherwise empty optional.
     * @throws ServiceException if {@code id} is invalid
     *                          or DaoException occurred.
     */
    Optional<CommentDto> findById(Long id) throws ServiceException;

    /**
     * Creates new {@code comment} in the storage using {@link CommentDto}.
     *
     * @param commentDto to be mapped to {@code comment}
     *                   and created in the storage.
     * @throws ServiceException if {@code commentDto} is invalid
     *                          or DaoException occurred.
     */
    void create(CommentDto commentDto) throws ServiceException;

    /**
     * Updates {@code comment} in the storage using {@link CommentDto}.
     *
     * @param commentDto to be mapped to {@code comment}
     *                   and updated in the storage.
     * @return {@code true} if {@code comment} was updated successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code commentDto} is invalid
     *                          or DaoException occurred.
     */
    boolean update(CommentDto commentDto) throws ServiceException;

    /**
     * Deletes {@code comment} in the storage using {@link CommentDto}.
     *
     * @param commentDto to be mapped to {@code comment}
     *                   and deleted in the storage.
     * @return {@code true} if {@code comment} was deleted successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code commentDto} is invalid
     *                          or DaoException occurred.
     */
    boolean delete(CommentDto commentDto) throws ServiceException;

    /**
     * Finds {@code comments} by {@code userDto}
     * and maps them to {@link CommentDto}.
     *
     * @param userDto {@code userDto} for search.
     * @return list of {@link CommentDto} for the given {@code userDto}.
     * @throws ServiceException if {@code userDto} is invalid
     *                          or DaoException occurred.
     */
    List<CommentDto> findByUserDtoOrderByCreatedAtDesc(UserDto userDto)
            throws ServiceException;

    /**
     * Finds {@code comments} by {@code dishDto}
     * and maps them to {@link CommentDto}.
     *
     * @param dishDto {@code dishDto} for search.
     * @return list of {@link CommentDto} for the given {@code dishDto}.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    List<CommentDto> findByDishDtoOrderByCreatedAtDesc(DishDto dishDto)
            throws ServiceException;

    /**
     * Finds {@code comments} with {@code limit and offset} by {@code dishDto}
     * and maps them to {@link CommentDto}.
     *
     * @param limit   returning number of DTOs.
     * @param offset  offset from storage.
     * @param dishDto {@code dishDto} for search.
     * @return list of {@link CommentDto} for the given {@code dishDto}.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    List<CommentDto> findByDishDtoOrderByCreatedAtDesc(DishDto dishDto,
                                                       long limit,
                                                       long offset)
            throws ServiceException;

    /**
     * Counts all comments grouped by rating.
     *
     * @param dishDto the dish, which comments are needed to be grouped.
     * @return map where key is rating, value is counted entities.
     * @throws ServiceException if DaoException occurred.
     */
    Map<Short, Long> countCommentsByDishGroupByRating(DishDto dishDto)
            throws ServiceException;

    /**
     * Calculates average dish rating.
     *
     * @param dishDto the dish, which average rating is needed to be count.
     * @return average dish rating.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    Double averageDishRating(DishDto dishDto) throws ServiceException;

    /**
     * Counts number of entities in the storage with the
     * given {@code dishDto}.
     *
     * @param dishDto the dish, which comments are needed to be counted.
     * @return number of entities with the given {@code dishDto}.
     * @throws ServiceException if {@code dishDto} is invalid
     *                          or DaoException occurred.
     */
    Long countByDishDto(DishDto dishDto) throws ServiceException;
}
