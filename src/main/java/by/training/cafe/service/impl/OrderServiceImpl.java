package by.training.cafe.service.impl;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.OrderDao;
import by.training.cafe.dao.Transaction;
import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dao.UserDao;
import by.training.cafe.dto.CreateOrderDto;
import by.training.cafe.dto.OrderDto;
import by.training.cafe.dto.UserDto;
import by.training.cafe.entity.Order;
import by.training.cafe.entity.User;
import by.training.cafe.service.OrderService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.mapper.CreateOrderDtoMapper;
import by.training.cafe.service.mapper.Mapper;
import by.training.cafe.service.mapper.OrderDtoMapper;
import by.training.cafe.service.mapper.UserDtoMapper;
import by.training.cafe.service.validator.CreateOrderDtoValidator;
import by.training.cafe.service.validator.OrderDtoValidator;
import by.training.cafe.service.validator.UserDtoValidator;
import by.training.cafe.service.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * The class {@code OrderServiceImpl} is a class
 * that implements {@link OrderService}.
 *
 * @author Nikita Romanov
 * @see OrderService
 */
public class OrderServiceImpl implements OrderService {

    private static final Logger log
            = LogManager.getLogger(OrderServiceImpl.class);
    private static final String RECEIVED_ORDER_DTO_LOG_MESSAGE
            = "Received orderDto: {}";
    private static final String RESULT_LIST_LOG_MESSAGE
            = "Result list: {}";
    private static final String ORDER_DTO_IS_INVALID_MESSAGE
            = "OrderDto is invalid: ";

    private final Mapper<Order, OrderDto> orderDtoMapper
            = OrderDtoMapper.getInstance();
    private final Mapper<Order, CreateOrderDto> createOrderDtoMapper
            = CreateOrderDtoMapper.getInstance();
    private final Validator<OrderDto> orderDtoValidator
            = OrderDtoValidator.getInstance();
    private final Validator<CreateOrderDto> createOrderDtoValidator
            = CreateOrderDtoValidator.getInstance();
    private final Mapper<User, UserDto> userDtoMapper =
            UserDtoMapper.getInstance();
    private final Validator<UserDto> userDtoValidator
            = UserDtoValidator.getInstance();

    private final TransactionFactory transactionFactory;

    public OrderServiceImpl(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    @Override
    public List<OrderDto> findAll() throws ServiceException {
        List<Order> orders;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            orders = orderDao.findAll();
            if (!orders.isEmpty()) {
                UserDao userDao = transaction.createDao(UserDao.class);
                for (Order order : orders) {
                    findUserAndSetToOrder(userDao, order);
                }
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findAll method", e);
        }
        List<OrderDto> result = orders.stream()
                .map(orderDtoMapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public List<OrderDto> findAll(long limit, long offset) throws ServiceException {
        if (limit < 1 || offset < 0) {
            throw new ServiceException(
                    "Limit or offset is invalid. Limit = %d, offset = %d"
                            .formatted(limit, offset));
        }
        List<Order> orders;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            orders = orderDao.findAll(limit, offset);
            if (!orders.isEmpty()) {
                UserDao userDao = transaction.createDao(UserDao.class);
                for (Order order : orders) {
                    findUserAndSetToOrder(userDao, order);
                }
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findAll method", e);
        }
        List<OrderDto> result = orders.stream()
                .map(orderDtoMapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public Optional<OrderDto> findById(Long id) throws ServiceException {
        log.debug("Received id: {}", id);
        if (id == null || id < 1) {
            throw new ServiceException("Id is invalid: " + id);
        }
        Optional<Order> maybeOrder;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            maybeOrder = orderDao.findById(id);
            if (maybeOrder.isPresent()) {
                UserDao userDao = transaction.createDao(UserDao.class);
                Order order = maybeOrder.get();
                findUserAndSetToOrder(userDao, order);
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findById method", e);
        }
        Optional<OrderDto> result = maybeOrder.map(orderDtoMapper::mapEntityToDto);
        log.debug("Result optional OrderDto: {}", result);
        return result;
    }

    @Override
    public OrderDto create(CreateOrderDto createOrderDto) throws ServiceException {
        log.debug("Received CreateOrderDto: {}", createOrderDto);
        if (!createOrderDtoValidator.isValid(createOrderDto)) {
            throw new ServiceException(
                    "CreateOrderDto is invalid: " + createOrderDto);
        }
        Order order = createOrderDtoMapper.mapDtoToEntity(createOrderDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            orderDao.create(order);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during create method", e);
        }
        OrderDto orderDto = orderDtoMapper.mapEntityToDto(order);
        log.debug("Result orderDto: {}", orderDto);
        return orderDto;
    }

    @Override
    public boolean update(OrderDto orderDto) throws ServiceException {
        log.debug(RECEIVED_ORDER_DTO_LOG_MESSAGE, orderDto);
        if (!orderDtoValidator.isValid(orderDto)) {
            throw new ServiceException(ORDER_DTO_IS_INVALID_MESSAGE + orderDto);
        }
        Order order = orderDtoMapper.mapDtoToEntity(orderDto);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            return orderDao.update(order);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during update method", e);
        }
    }

    @Override
    public boolean delete(OrderDto orderDto) throws ServiceException {
        log.debug(RECEIVED_ORDER_DTO_LOG_MESSAGE, orderDto);
        if (!orderDtoValidator.isValid(orderDto)) {
            throw new ServiceException(ORDER_DTO_IS_INVALID_MESSAGE + orderDto);
        }
        Long id = orderDto.getId();
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            return orderDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("Dao exception during delete method", e);
        }
    }

    @Override
    public List<OrderDto> findByUserDto(UserDto userDto) throws ServiceException {
        log.debug("Received userDto: {}", userDto);
        if (!userDtoValidator.isValid(userDto)) {
            throw new ServiceException("UserDto is invalid: " + userDto);
        }
        List<Order> orders;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            orders = orderDao.findByUserId(userDto.getId());
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findByUserId method", e);
        }
        User user = userDtoMapper.mapDtoToEntity(userDto);
        orders.forEach(order -> order.setUser(user));
        List<OrderDto> result = orders.stream()
                .map(orderDtoMapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public List<OrderDto> findByCreatedAtBetween(Timestamp from,
                                                 Timestamp to)
            throws ServiceException {
        log.debug("Received from = {} and to = {}", from, to);
        if (from == null || to == null || to.before(from)) {
            throw new ServiceException(
                    "From or to timestamp is invalid. From = %s. To = %s"
                            .formatted(from, to));
        }
        List<Order> orders;
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            orders = orderDao.findByCreatedAtBetween(from, to);
            if (!orders.isEmpty()) {
                UserDao userDao = transaction.createDao(UserDao.class);
                for (Order order : orders) {
                    findUserAndSetToOrder(userDao, order);
                }
            }
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during findByUserId method", e);
        }
        List<OrderDto> result = orders.stream()
                .map(orderDtoMapper::mapEntityToDto)
                .toList();
        log.debug(RESULT_LIST_LOG_MESSAGE, result);
        return result;
    }

    @Override
    public Long countOrders() throws ServiceException {
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            return orderDao.count();
        } catch (DaoException e) {
            throw new ServiceException(
                    "Dao exception during countOrders method", e);
        }
    }

    private void findUserAndSetToOrder(UserDao userDao, Order order)
            throws DaoException, ServiceException {
        Long userId = order.getUser().getId();
        Optional<User> maybeUser = userDao.findById(userId);
        User user = maybeUser.orElseThrow(ServiceException::new);
        order.setUser(user);
    }
}
