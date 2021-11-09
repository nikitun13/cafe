package by.training.cafe.service.impl;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.Transaction;
import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dao.UserDao;
import by.training.cafe.dto.CreateUserDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.User;
import by.training.cafe.service.EncoderService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.UserService;
import by.training.cafe.service.mapper.CreateUserDtoMapper;
import by.training.cafe.service.mapper.Mapper;
import by.training.cafe.service.mapper.UserDtoMapper;
import by.training.cafe.service.validator.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

/**
 * The class {@code UserServiceImpl} is a class
 * that implements {@link UserService}.
 *
 * @author Nikita Romanov
 * @see UserService
 */
public class UserServiceImpl implements UserService {

    private static final Logger log
            = LogManager.getLogger(UserServiceImpl.class);
    private static final String RECEIVED_USER_DTO_LOG_MESSAGE
            = "Received UserDto: {}";
    private static final String RESULT_LIST_LOG_MESSAGE
            = "Result list: {}";
    private static final String USER_DTO_IS_INVALID_MESSAGE
            = "UserDto is invalid: ";

    private final Mapper<User, UserDto> userDtoMapper
            = UserDtoMapper.getInstance();
    private final Mapper<User, CreateUserDto> createUserDtoMapper
            = CreateUserDtoMapper.getInstance();
    private final Validator<String> emailValidator
            = EmailValidator.getInstance();
    private final Validator<String> passwordValidator
            = PasswordValidator.getInstance();
    private final Validator<CreateUserDto> createUserDtoValidator
            = CreateUserDtoValidator.getInstance();
    private final Validator<UserDto> userDtoValidator
            = UserDtoValidator.getInstance();

    private final EncoderService encoder;
    private final TransactionFactory transactionFactory;

    public UserServiceImpl(TransactionFactory transactionFactory,
                           EncoderService encoder) {
        this.transactionFactory = transactionFactory;
        this.encoder = encoder;
    }

    @Override
    public List<UserDto> findAll() throws ServiceException {
        List<User> users;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            UserDao userDao = transaction.createDao(UserDao.class);
            users = userDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findAll method", e);
        }
        List<UserDto> result = users.stream()
                .map(userDtoMapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public Optional<UserDto> findById(Long id) throws ServiceException {
        log.debug("Received id: {}", id);
        if (id == null || id < 1) {
            throw new ServiceException("Id is invalid: " + id);
        }
        Optional<User> maybeUser;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            UserDao userDao = transaction.createDao(UserDao.class);
            maybeUser = userDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findById method", e);
        }
        Optional<UserDto> result = maybeUser.map(userDtoMapper::mapEntityToDto);
        log.debug("Result optional UserDto: {}", result);
        return result;
    }

    @Override
    public Optional<UserDto> signIn(String email, String password)
            throws ServiceException {
        log.debug("Received email = {} and password = {}",
                email, password);
        if (!emailValidator.isValid(email)
                || !passwordValidator.isValid(password)) {
            throw new ServiceException(
                    "Email or password is invalid: email = %s paswword = %s"
                            .formatted(email, password)
            );
        }
        Optional<User> maybeUser;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            UserDao userDao = transaction.createDao(UserDao.class);
            maybeUser = userDao.findByEmail(email);
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during signIn method", e);
        }
        if (maybeUser.isPresent()) {
            String encodedPassword = maybeUser.get().getPassword();
            if (!encoder.matches(password, encodedPassword)) {
                maybeUser = Optional.empty();
            }
        }
        Optional<UserDto> result
                = maybeUser.map(userDtoMapper::mapEntityToDto);
        log.debug("Result optional UserDto: {}", result);
        return result;
    }

    @Override
    public UserDto signUp(CreateUserDto createUserDto) throws ServiceException {
        log.debug("Received createUserDto: {}", createUserDto);
        if (!createUserDtoValidator.isValid(createUserDto)) {
            throw new ServiceException(
                    "CreateUserDto is invalid: " + createUserDto);
        }
        User user = createUserDtoMapper.mapDtoToEntity(createUserDto);
        String rawPassword = user.getPassword();
        String encodedPassword = encoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            UserDao userDao = transaction.createDao(UserDao.class);
            userDao.create(user);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during signUp method", e);
        }
        UserDto userDto = userDtoMapper.mapEntityToDto(user);
        log.debug("Result userDto: {}", userDto);
        return userDto;
    }

    @Override
    public boolean update(UserDto userDto) throws ServiceException {
        log.debug(RECEIVED_USER_DTO_LOG_MESSAGE, userDto);
        if (!userDtoValidator.isValid(userDto)) {
            throw new ServiceException(USER_DTO_IS_INVALID_MESSAGE + userDto);
        }
        User user = userDtoMapper.mapDtoToEntity(userDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            UserDao userDao = transaction.createDao(UserDao.class);
            return userDao.update(user);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during update method", e);
        }
    }

    @Override
    public boolean updatePassword(UserDto userDto,
                                  String oldPassword, String newPassword)
            throws ServiceException {
        log.debug("Received userDto = {}, oldPassword = {}, newPassword = {}",
                userDto, oldPassword, newPassword);
        if (!userDtoValidator.isValid(userDto)
                || !passwordValidator.isValid(oldPassword)
                || !passwordValidator.isValid(newPassword)) {
            throw new ServiceException(
                    ("UserDto or oldPassword or newPassword is invalid. "
                            + "UserDto = %s. OldPassword = %s. NewPassword = %s")
                            .formatted(userDto, oldPassword, newPassword)
            );
        }
        try (Transaction transaction = transactionFactory.createTransaction()) {
            UserDao userDao = transaction.createDao(UserDao.class);
            Optional<User> maybeUser = userDao.findById(userDto.getId());
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                String encodedPassword = user.getPassword();
                if (encoder.matches(oldPassword, encodedPassword)) {
                    log.debug("oldPassword matches");
                    return userDao.updatePassword(
                            user.getId(),
                            encoder.encode(newPassword));
                }
                log.debug("oldPassword doesn't match");
            }
            return false;
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during updatePassword method", e);
        }
    }

    @Override
    public boolean delete(UserDto userDto) throws ServiceException {
        log.debug(RECEIVED_USER_DTO_LOG_MESSAGE, userDto);
        if (userDto == null || userDto.getId() == null || userDto.getId() < 1) {
            throw new ServiceException(USER_DTO_IS_INVALID_MESSAGE + userDto);
        }
        Long id = userDto.getId();
        try (Transaction transaction = transactionFactory.createTransaction()) {
            UserDao userDao = transaction.createDao(UserDao.class);
            return userDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during delete method", e);
        }
    }
}