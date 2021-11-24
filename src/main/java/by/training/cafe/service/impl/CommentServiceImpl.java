package by.training.cafe.service.impl;

import by.training.cafe.dao.CommentDao;
import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.DishDao;
import by.training.cafe.dao.Transaction;
import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dao.UserDao;
import by.training.cafe.dto.CommentDto;
import by.training.cafe.dto.DishDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.Comment;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.User;
import by.training.cafe.service.CommentService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.mapper.CommentMapper;
import by.training.cafe.service.mapper.DishMapper;
import by.training.cafe.service.mapper.Mapper;
import by.training.cafe.service.mapper.UserDtoMapper;
import by.training.cafe.service.validator.CommentDtoValidator;
import by.training.cafe.service.validator.DishDtoValidator;
import by.training.cafe.service.validator.UserDtoValidator;
import by.training.cafe.service.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * The class {@code CommentServiceImpl} is a class
 * that implements {@link CommentService}.
 *
 * @author Nikita Romanov
 * @see CommentService
 */
public class CommentServiceImpl implements CommentService {

    private static final Logger log
            = LogManager.getLogger(CommentServiceImpl.class);
    private static final String RECEIVED_COMMENT_DTO_LOG_MESSAGE
            = "Received CommentDto: {}";
    private static final String RECEIVED_DISH_DTO_LOG_MESSAGE
            = "Received dishDto: {}";
    private static final String RESULT_LIST_LOG_MESSAGE
            = "Result list: {}";
    private static final String COMMENT_DTO_IS_INVALID_MESSAGE
            = "CommentDto is invalid: ";
    private static final String DISH_DTO_IS_INVALID_MESSAGE
            = "DishDto is invalid: ";
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final Mapper<Comment, CommentDto> mapper
            = CommentMapper.getInstance();
    private final Validator<CommentDto> commentDtoValidator
            = CommentDtoValidator.getInstance();
    private final Validator<DishDto> dishDtoValidator
            = DishDtoValidator.getInstance();
    private final Mapper<Dish, DishDto> dishDishDtoMapper
            = DishMapper.getInstance();
    private final Validator<UserDto> userDtoValidator
            = UserDtoValidator.getInstance();
    private final Mapper<User, UserDto> userDtoMapper
            = UserDtoMapper.getInstance();
    private final TransactionFactory transactionFactory;

    public CommentServiceImpl(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    @Override
    public List<CommentDto> findAll() throws ServiceException {
        List<Comment> comments;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            comments = commentDao.findAll();
            setUserAndDishToComment(comments, transaction);
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findAll method", e);
        }
        List<CommentDto> result = comments.stream()
                .map(mapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }


    @Override
    public List<CommentDto> findAll(long limit, long offset)
            throws ServiceException {
        if (limit < 1 || offset < 0) {
            throw new ServiceException(
                    "Limit or offset is invalid. Limit = %d, offset = %d"
                            .formatted(limit, offset));
        }
        List<Comment> comments;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            comments = commentDao.findAll(limit, offset);
            setUserAndDishToComment(comments, transaction);
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findAll"
                            + " with limit and offset method", e);
        }
        List<CommentDto> result = comments.stream()
                .map(mapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public Long count() throws ServiceException {
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            return commentDao.count();
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during count method", e);
        }
    }

    @Override
    public Optional<CommentDto> findById(Long id) throws ServiceException {
        log.debug("Received id: {}", id);
        if (id == null || id < 1) {
            throw new ServiceException("Id is invalid: " + id);
        }
        Optional<Comment> maybeComment;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            maybeComment = commentDao.findById(id);
            if (maybeComment.isPresent()) {
                Comment comment = maybeComment.get();
                UserDao userDao = transaction.createDao(UserDao.class);
                DishDao dishDao = transaction.createDao(DishDao.class);
                findUserAndSetToComment(userDao, comment);
                findDishAndSetToComment(dishDao, comment);
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findById method", e);
        }
        Optional<CommentDto> result = maybeComment.map(mapper::mapEntityToDto);
        log.debug("Result optional CommentDto: {}", result);
        return result;
    }

    @Override
    public void create(CommentDto commentDto) throws ServiceException {
        log.debug(RECEIVED_COMMENT_DTO_LOG_MESSAGE, commentDto);
        if (!commentDtoValidator.isValid(commentDto)
                || commentDto.getId() != null) {
            throw new ServiceException(
                    COMMENT_DTO_IS_INVALID_MESSAGE + commentDto);
        }
        Comment comment = mapper.mapDtoToEntity(commentDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            commentDao.create(comment);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during create method", e);
        }
        commentDto.setId(comment.getId());
    }

    @Override
    public boolean update(CommentDto commentDto) throws ServiceException {
        log.debug(RECEIVED_COMMENT_DTO_LOG_MESSAGE, commentDto);
        if (!commentDtoValidator.isValid(commentDto)
                || commentDto.getId() == null
                || commentDto.getId() < 1) {
            throw new ServiceException(
                    COMMENT_DTO_IS_INVALID_MESSAGE + commentDto);
        }
        Comment comment = mapper.mapDtoToEntity(commentDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            return commentDao.update(comment);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during update method", e);
        }
    }

    @Override
    public boolean delete(CommentDto commentDto) throws ServiceException {
        log.debug(RECEIVED_COMMENT_DTO_LOG_MESSAGE, commentDto);
        if (!commentDtoValidator.isValid(commentDto)
                || commentDto.getId() == null
                || commentDto.getId() < 1) {
            throw new ServiceException(
                    COMMENT_DTO_IS_INVALID_MESSAGE + commentDto);
        }
        Long id = commentDto.getId();
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            return commentDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during delete method", e);
        }
    }

    @Override
    public List<CommentDto> findByUserDtoOrderByCreatedAtDesc(UserDto userDto)
            throws ServiceException {
        log.debug("Received userDto: {}", userDto);
        if (!userDtoValidator.isValid(userDto)) {
            throw new ServiceException("UserDto is invalid: " + userDto);
        }
        List<Comment> comments;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            comments = commentDao.findByUserIdOrderByCreatedAtDesc(userDto.getId());
            DishDao dishDao = transaction.createDao(DishDao.class);
            for (Comment comment : comments) {
                findDishAndSetToComment(dishDao, comment);
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during "
                            + "findByUserDtoOrderByCreatedAtDesc method", e);
        }
        User user = userDtoMapper.mapDtoToEntity(userDto);
        comments.forEach(comment -> comment.setUser(user));
        List<CommentDto> result = comments.stream()
                .map(mapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public List<CommentDto> findByDishDtoOrderByCreatedAtDesc(DishDto dishDto)
            throws ServiceException {
        log.debug(RECEIVED_DISH_DTO_LOG_MESSAGE, dishDto);
        if (!dishDtoValidator.isValid(dishDto)) {
            throw new ServiceException(DISH_DTO_IS_INVALID_MESSAGE + dishDto);
        }
        List<Comment> comments;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            comments = commentDao.findByDishIdOrderByCreatedAtDesc(
                    dishDto.getId());
            UserDao userDao = transaction.createDao(UserDao.class);
            for (Comment comment : comments) {
                findUserAndSetToComment(userDao, comment);
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during"
                            + " findByDishDtoOrderByCreatedAtDesc method", e);
        }
        Dish dish = dishDishDtoMapper.mapDtoToEntity(dishDto);
        comments.forEach(comment -> comment.setDish(dish));
        List<CommentDto> result = comments.stream()
                .map(mapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public List<CommentDto> findByDishDtoOrderByCreatedAtDesc(DishDto dishDto,
                                                              long limit,
                                                              long offset)
            throws ServiceException {
        log.debug(RECEIVED_DISH_DTO_LOG_MESSAGE, dishDto);
        log.debug("limit = {}, offset = {}", limit, offset);
        if (!dishDtoValidator.isValid(dishDto)) {
            throw new ServiceException(DISH_DTO_IS_INVALID_MESSAGE + dishDto);
        }
        if (limit < 1 || offset < 0) {
            throw new ServiceException(
                    "Limit or offset is invalid. Limit = %d, offset = %d"
                            .formatted(limit, offset));
        }
        List<Comment> comments;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            comments = commentDao.findByDishIdOrderByCreatedAtDesc(
                    dishDto.getId(), limit, offset);
            UserDao userDao = transaction.createDao(UserDao.class);
            for (Comment comment : comments) {
                findUserAndSetToComment(userDao, comment);
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findByDishDtoOrderByCreatedAtDesc"
                            + " method with limit and offset", e);
        }
        Dish dish = dishDishDtoMapper.mapDtoToEntity(dishDto);
        comments.forEach(comment -> comment.setDish(dish));
        List<CommentDto> result = comments.stream()
                .map(mapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public Map<Short, Long> countCommentsByDishGroupByRating(DishDto dishDto)
            throws ServiceException {
        log.debug(RECEIVED_DISH_DTO_LOG_MESSAGE, dishDto);
        if (!dishDtoValidator.isValid(dishDto)) {
            throw new ServiceException(DISH_DTO_IS_INVALID_MESSAGE + dishDto);
        }
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            Map<Short, Long> groupByRating
                    = commentDao.countCommentsByDishIdGroupByRating(dishDto.getId());
            Map<Short, Long> result = new TreeMap<>(Comparator.reverseOrder());
            result.putAll(groupByRating);
            Function<Short, Long> computeAbsent = key -> 0L;
            for (int i = MIN_RATING; i <= MAX_RATING; ++i) {
                result.computeIfAbsent((short) i, computeAbsent);
            }
            log.debug("Result groups: {}", result);
            return result;
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during countGroupByRating method", e);
        }
    }

    @Override
    public Double averageDishRating(DishDto dishDto) throws ServiceException {
        log.debug(RECEIVED_DISH_DTO_LOG_MESSAGE, dishDto);
        if (!dishDtoValidator.isValid(dishDto)) {
            throw new ServiceException(DISH_DTO_IS_INVALID_MESSAGE + dishDto);
        }
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            return commentDao.averageDishRating(dishDto.getId());
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during countAverageDishRating method", e);
        }
    }

    @Override
    public Long countByDishDto(DishDto dishDto) throws ServiceException {
        log.debug(RECEIVED_DISH_DTO_LOG_MESSAGE, dishDto);
        if (!dishDtoValidator.isValid(dishDto)) {
            throw new ServiceException(DISH_DTO_IS_INVALID_MESSAGE + dishDto);
        }
        try (Transaction transaction = transactionFactory.createTransaction()) {
            CommentDao commentDao = transaction.createDao(CommentDao.class);
            return commentDao.countByDishId(dishDto.getId());
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during countByDishId method", e);
        }
    }

    private void setUserAndDishToComment(List<Comment> comments,
                                         Transaction transaction)
            throws DaoException, ServiceException {
        if (!comments.isEmpty()) {
            UserDao userDao = transaction.createDao(UserDao.class);
            DishDao dishDao = transaction.createDao(DishDao.class);
            for (Comment comment : comments) {
                findUserAndSetToComment(userDao, comment);
                findDishAndSetToComment(dishDao, comment);
            }
        }
    }

    private void findUserAndSetToComment(UserDao userDao, Comment comment)
            throws DaoException, ServiceException {
        Long userId = comment.getUser().getId();
        Optional<User> maybeUser = userDao.findById(userId);
        User user = maybeUser.orElseThrow(ServiceException::new);
        comment.setUser(user);
    }

    private void findDishAndSetToComment(DishDao dishDao, Comment comment)
            throws DaoException, ServiceException {
        Long dishId = comment.getDish().getId();
        Optional<Dish> maybeDish = dishDao.findById(dishId);
        Dish dish = maybeDish.orElseThrow(ServiceException::new);
        comment.setDish(dish);
    }
}
