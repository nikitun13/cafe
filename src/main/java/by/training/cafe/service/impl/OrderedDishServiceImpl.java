package by.training.cafe.service.impl;

import by.training.cafe.dao.*;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.OrderedDishDto;
import by.training.cafe.entity.Dish;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.OrderedDish;
import by.training.cafe.entity.User;
import by.training.cafe.service.OrderedDishService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.mapper.Mapper;
import by.training.cafe.service.mapper.OrderedDishMapper;
import by.training.cafe.service.validator.OrderDtoValidator;
import by.training.cafe.service.validator.OrderedDishDtoValidator;
import by.training.cafe.service.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;

/**
 * The class {@code OrderedDishServiceImpl} is a class
 * that implements {@link OrderedDishService}.
 *
 * @author Nikita Romanov
 * @see OrderedDishService
 */
public class OrderedDishServiceImpl implements OrderedDishService {

    private static final Logger log
            = LogManager.getLogger(OrderedDishServiceImpl.class);
    private static final String RECEIVED_ORDERED_DISH_DTO_LOG_MESSAGE
            = "Received orderedDishDto: {}";
    private static final String RESULT_LIST_LOG_MESSAGE
            = "Result list: {}";
    private static final String ORDERED_DISH_DTO_IS_INVALID_MESSAGE
            = "OrderedDishDto is invalid: ";

    private final Mapper<OrderedDish, OrderedDishDto> mapper
            = OrderedDishMapper.getInstance();
    private final Validator<OrderedDishDto> orderedDishDtoValidator
            = OrderedDishDtoValidator.getInstance();
    private final Validator<OrderDto> orderDtoValidator
            = OrderDtoValidator.getInstance();
    private final TransactionFactory transactionFactory;

    public OrderedDishServiceImpl(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    @Override
    public List<OrderedDishDto> findAll() throws ServiceException {
        List<OrderedDish> orderedDishes;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderedDishDao orderedDishDao
                    = transaction.createDao(OrderedDishDao.class);
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            DishDao dishDao = transaction.createDao(DishDao.class);
            UserDao userDao = transaction.createDao(UserDao.class);
            orderedDishes = orderedDishDao.findAll();
            for (OrderedDish orderedDish : orderedDishes) {
                findOrderAndSetToOrderedDish(orderDao, userDao, orderedDish);
                findDishAndSetToOrderedDish(dishDao, orderedDish);
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findAll method", e);
        }

        List<OrderedDishDto> result = orderedDishes.stream()
                .map(mapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public Optional<OrderedDishDto> findById(Long orderId, Long dishId)
            throws ServiceException {
        log.debug("Received orderId = {} and dishId = {}", orderId, dishId);
        if (orderId == null || orderId < 1 || dishId == null || dishId < 1) {
            throw new ServiceException(
                    "OrderId or dishId is invalid. Order id = %d, dish id = %d"
                            .formatted(orderId, dishId));
        }
        Optional<OrderedDish> maybeOrderedDish;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderedDishDao orderedDishDao
                    = transaction.createDao(OrderedDishDao.class);
            maybeOrderedDish = orderedDishDao.findById(
                    new SimpleEntry<>(orderId, dishId));
            if (maybeOrderedDish.isPresent()) {
                OrderDao orderDao = transaction.createDao(OrderDao.class);
                DishDao dishDao = transaction.createDao(DishDao.class);
                UserDao userDao = transaction.createDao(UserDao.class);
                OrderedDish orderedDish = maybeOrderedDish.get();
                findDishAndSetToOrderedDish(dishDao, orderedDish);
                findOrderAndSetToOrderedDish(orderDao, userDao, orderedDish);
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findById method", e);
        }
        Optional<OrderedDishDto> result
                = maybeOrderedDish.map(mapper::mapEntityToDto);
        log.debug("Result optional OrderedDishDto: {}", result);
        return result;
    }

    @Override
    public void create(OrderedDishDto orderedDishDto) throws ServiceException {
        log.debug(RECEIVED_ORDERED_DISH_DTO_LOG_MESSAGE, orderedDishDto);
        if (!orderedDishDtoValidator.isValid(orderedDishDto)) {
            throw new ServiceException(
                    ORDERED_DISH_DTO_IS_INVALID_MESSAGE + orderedDishDto);
        }
        OrderedDish orderedDish = mapper.mapDtoToEntity(orderedDishDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderedDishDao orderedDishDao
                    = transaction.createDao(OrderedDishDao.class);
            orderedDishDao.create(orderedDish);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during create method", e);
        }
    }

    @Override
    public boolean update(OrderedDishDto orderedDishDto) throws ServiceException {
        log.debug(RECEIVED_ORDERED_DISH_DTO_LOG_MESSAGE, orderedDishDto);
        if (!orderedDishDtoValidator.isValid(orderedDishDto)) {
            throw new ServiceException(
                    ORDERED_DISH_DTO_IS_INVALID_MESSAGE + orderedDishDto);
        }
        OrderedDish orderedDish = mapper.mapDtoToEntity(orderedDishDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderedDishDao orderedDishDao
                    = transaction.createDao(OrderedDishDao.class);
            return orderedDishDao.update(orderedDish);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during update method", e);
        }
    }

    @Override
    public boolean delete(OrderedDishDto orderedDishDto) throws ServiceException {
        log.debug(RECEIVED_ORDERED_DISH_DTO_LOG_MESSAGE, orderedDishDto);
        if (!orderedDishDtoValidator.isValid(orderedDishDto)) {
            throw new ServiceException(
                    ORDERED_DISH_DTO_IS_INVALID_MESSAGE + orderedDishDto);
        }
        Long orderId = orderedDishDto.getOrder().getId();
        Long dishId = orderedDishDto.getDish().getId();
        SimpleEntry<Long, Long> id = new SimpleEntry<>(orderId, dishId);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderedDishDao orderedDishDao
                    = transaction.createDao(OrderedDishDao.class);
            return orderedDishDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during delete method", e);
        }
    }

    @Override
    public List<OrderedDishDto> findByOrderDto(OrderDto orderDto)
            throws ServiceException {
        log.debug("Received orderDto: {}", orderDto);
        if (!orderDtoValidator.isValid(orderDto)) {
            throw new ServiceException("OrderDto is invalid: " + orderDto);
        }
        Long orderId = orderDto.getId();
        List<OrderedDish> orderedDishes;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderedDishDao orderedDishDao
                    = transaction.createDao(OrderedDishDao.class);
            orderedDishes = orderedDishDao.findByOrderId(orderId);
            if (!orderedDishes.isEmpty()) {
                DishDao dishDao = transaction.createDao(DishDao.class);
                for (OrderedDish orderedDish : orderedDishes) {
                    findDishAndSetToOrderedDish(dishDao, orderedDish);
                }
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findByOrder method", e);
        }
        List<OrderedDishDto> result = orderedDishes.stream()
                .map(mapper::mapEntityToDto)
                .toList();
        result.forEach(orderedDishDto -> orderedDishDto.setOrder(orderDto));
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    private void findOrderAndSetToOrderedDish(OrderDao orderDao, UserDao userDao,
                                              OrderedDish orderedDish)
            throws DaoException, ServiceException {
        Long orderId = orderedDish.getOrder().getId();
        Optional<Order> maybeOrder = orderDao.findById(orderId);
        Order order = maybeOrder.orElseThrow(ServiceException::new);
        Optional<User> maybeUser = userDao.findById(order.getUser().getId());
        User user = maybeUser.orElseThrow(ServiceException::new);
        order.setUser(user);
        orderedDish.setOrder(order);
    }

    private void findDishAndSetToOrderedDish(DishDao dishDao,
                                             OrderedDish orderedDish)
            throws DaoException, ServiceException {
        Long dishId = orderedDish.getDish().getId();
        Optional<Dish> maybeDish = dishDao.findById(dishId);
        Dish dish = maybeDish.orElseThrow(ServiceException::new);
        orderedDish.setDish(dish);
    }
}
