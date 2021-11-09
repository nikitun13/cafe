package by.training.cafe.service;

import by.training.cafe.dto.CreateUserDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * The class {@code UserService} is a class that
 * implements {@link Service}.<br/>
 * Provides different business logic with {@link User} entities
 * using {@link UserDto} and {@link CreateUserDto}.
 *
 * @author Nikita Romanov
 * @see Service
 * @see User
 * @see UserDto
 * @see CreateUserDto
 */
public interface UserService extends Service {

    /**
     * Returns all {@code users} from storage
     * mapped to {@link UserDto}.
     *
     * @return list of all {@code UserDtos}.
     * @throws ServiceException if DaoException occurred.
     */
    List<UserDto> findAll() throws ServiceException;

    /**
     * Finds {@code users} by {@code id} and maps it to {@link UserDto}.
     *
     * @param id entity {@code id}.
     * @return optional {@link UserDto}. If entity was found
     * optional contains {@link UserDto}, otherwise empty optional.
     * @throws ServiceException if {@code id} is invalid
     *                          or DaoException occurred.
     */
    Optional<UserDto> findById(Long id) throws ServiceException;

    /**
     * Authenticates the user by {@code email} and {@code password}.
     *
     * @param email    user {@code email}.
     * @param password user {@code password}.
     * @return {@link UserDto} if authenticates successfully,
     * empty optional otherwise.
     * @throws ServiceException if {@code email} or {@code password}
     *                          is invalid or DaoException occurred.
     */
    Optional<UserDto> signIn(String email, String password)
            throws ServiceException;

    /**
     * Registers new {@code user}.
     *
     * @param createUserDto new {@code user} create data.
     * @return {@link UserDto} of new created {@code user}.
     * @throws ServiceException if {@code createUserDto} is invalid
     *                          or DaoException occurred.
     */
    UserDto signUp(CreateUserDto createUserDto) throws ServiceException;

    /**
     * Updates {@code user} in the storage.
     *
     * @param userDto updated {@code user} data.
     * @return {@code true} if {@code user} was updated successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code userDto} is invalid
     *                          or DaoException occurred.
     */
    boolean update(UserDto userDto) throws ServiceException;

    /**
     * Updates {@code user} password.
     *
     * @param userDto     {@code user} to update password.
     * @param oldPassword old {@code user} password to be updated.
     * @param newPassword new password to be set.
     * @return {@code true} if {@code user} password was updated
     * successfully, {@code false} otherwise.
     * @throws ServiceException if {@code userDto} or {@code oldPassword}
     *                          or {@code newPassword} is invalid
     *                          or DaoException occurred.
     */
    boolean updatePassword(UserDto userDto, String oldPassword,
                           String newPassword) throws ServiceException;

    /**
     * Deletes {@code user} from the storage.
     *
     * @param userDto {@code user} to be deleted.
     * @return {@code true} if {@code user} was deleted successfully,
     * {@code false} otherwise.
     * @throws ServiceException if {@code userDto} is invalid
     *                          or DaoException occurred.
     */
    boolean delete(UserDto userDto) throws ServiceException;
}
