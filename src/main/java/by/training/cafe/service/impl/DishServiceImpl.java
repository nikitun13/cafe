package by.training.cafe.service.impl;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.DishDao;
import by.training.cafe.dao.Transaction;
import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dto.DishDto;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.DishCategory;
import by.training.cafe.service.DishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.mapper.DishMapper;
import by.training.cafe.service.mapper.Mapper;
import by.training.cafe.service.validator.DishDtoValidator;
import by.training.cafe.service.validator.StringValidator;
import by.training.cafe.service.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

/**
 * The class {@code DishServiceImpl} is a class
 * that implements {@link DishService}.
 *
 * @author Nikita Romanov
 * @see DishService
 */
public class DishServiceImpl implements DishService {

    private static final Logger log
            = LogManager.getLogger(DishServiceImpl.class);
    private static final String RECEIVED_DISH_DTO_LOG_MESSAGE
            = "Received dishDto: {}";
    private static final String RESULT_LIST_LOG_MESSAGE
            = "Result list: {}";
    private static final String DISH_DTO_IS_INVALID_MESSAGE
            = "DishDto is invalid: ";

    private final Mapper<Dish, DishDto> mapper
            = DishMapper.getInstance();
    private final Validator<DishDto> dishDtoValidator
            = DishDtoValidator.getInstance();
    private final Validator<String> stringValidator
            = StringValidator.getInstance();
    private final TransactionFactory transactionFactory;

    public DishServiceImpl(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    @Override
    public List<DishDto> findAll() throws ServiceException {
        List<Dish> dishes;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            DishDao dishDao = transaction.createDao(DishDao.class);
            dishes = dishDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findAll method", e);
        }
        List<DishDto> result = dishes.stream()
                .map(mapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public Optional<DishDto> findById(Long id) throws ServiceException {
        log.debug("Received id: {}", id);
        if (id == null || id < 1) {
            throw new ServiceException("Id is invalid: " + id);
        }
        Optional<Dish> maybeDish;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            DishDao dishDao = transaction.createDao(DishDao.class);
            maybeDish = dishDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findById method", e);
        }
        Optional<DishDto> result = maybeDish.map(mapper::mapEntityToDto);
        log.debug("Result optional dishDto: {}", result);
        return result;
    }

    @Override
    public void create(DishDto dishDto) throws ServiceException {
        log.debug(RECEIVED_DISH_DTO_LOG_MESSAGE, dishDto);
        if (!dishDtoValidator.isValid(dishDto) || dishDto.getId() != null) {
            throw new ServiceException(DISH_DTO_IS_INVALID_MESSAGE + dishDto);
        }
        Dish dish = mapper.mapDtoToEntity(dishDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            DishDao dishDao = transaction.createDao(DishDao.class);
            dishDao.create(dish);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during create method", e);
        }
        dishDto.setId(dish.getId());
    }

    @Override
    public boolean update(DishDto dishDto) throws ServiceException {
        log.debug(RECEIVED_DISH_DTO_LOG_MESSAGE, dishDto);
        if (!dishDtoValidator.isValid(dishDto)
                || dishDto.getId() == null
                || dishDto.getId() < 1) {
            throw new ServiceException(DISH_DTO_IS_INVALID_MESSAGE + dishDto);
        }
        Dish dish = mapper.mapDtoToEntity(dishDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            DishDao dishDao = transaction.createDao(DishDao.class);
            return dishDao.update(dish);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during update method", e);
        }
    }

    @Override
    public boolean delete(DishDto dishDto) throws ServiceException {
        log.debug(RECEIVED_DISH_DTO_LOG_MESSAGE, dishDto);
        if (!dishDtoValidator.isValid(dishDto)
                || dishDto.getId() == null
                || dishDto.getId() < 1) {
            throw new ServiceException(DISH_DTO_IS_INVALID_MESSAGE + dishDto);
        }
        Long id = dishDto.getId();
        try (Transaction transaction = transactionFactory.createTransaction()) {
            DishDao dishDao = transaction.createDao(DishDao.class);
            return dishDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during delete method", e);
        }
    }

    @Override
    public Map<String, List<DishDto>> groupByCategory(List<DishDto> dishes) {
        Map<String, List<DishDto>> resultMap = dishes.stream()
                .filter(dishDto -> dishDto.getCategory() != null)
                .collect(groupingBy(DishDto::getCategory));
        log.debug("result map: {}", resultMap);
        return resultMap;
    }

    @Override
    public List<DishDto> findByNameOrDescriptionLike(String str)
            throws ServiceException {
        log.debug("Received str: {}", str);
        if (!stringValidator.isValid(str) || str.length() > 64) {
            throw new ServiceException("String is invalid: " + str);
        }
        List<String> words = Arrays.asList(str.strip().split("\\s+"));
        log.debug("Result words: {}", words);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            DishDao dishDao = transaction.createDao(DishDao.class);
            List<DishDto> result = dishDao.findByNameOrDescriptionLike(words)
                    .stream()
                    .map(mapper::mapEntityToDto)
                    .toList();
            log.debug(RESULT_LIST_LOG_MESSAGE, result);
            return result;
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findByNameOrDescriptionLike method", e);
        }
    }

    @Override
    public List<DishDto> findByCategory(String category)
            throws ServiceException {
        log.debug("Received str: {}", category);
        if (!stringValidator.isValid(category)
                || !DishCategory.contains(category.toUpperCase())) {
            throw new ServiceException("String is invalid: " + category);
        }
        category = category.toUpperCase();
        try (Transaction transaction = transactionFactory.createTransaction()) {
            DishDao dishDao = transaction.createDao(DishDao.class);
            List<DishDto> result = dishDao.findByCategory(category).stream()
                    .map(mapper::mapEntityToDto)
                    .toList();
            log.debug(RESULT_LIST_LOG_MESSAGE, result);
            return result;
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findByCategory method", e);
        }
    }
}
