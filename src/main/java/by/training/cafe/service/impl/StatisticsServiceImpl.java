package by.training.cafe.service.impl;

import by.training.cafe.dao.DaoException;
import by.training.cafe.dao.DishDao;
import by.training.cafe.dao.OrderDao;
import by.training.cafe.dao.OrderedDishDao;
import by.training.cafe.dao.Transaction;
import by.training.cafe.dao.TransactionFactory;
import by.training.cafe.dto.DishDto;
import by.training.cafe.entity.Dish;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.StatisticsService;
import by.training.cafe.service.mapper.DishMapper;
import by.training.cafe.service.mapper.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;


/**
 * The class {@code StatisticsServiceImpl} is a class
 * that implements {@link StatisticsService}.
 *
 * @author Nikita Romanov
 * @see StatisticsService
 */
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger log
            = LogManager.getLogger(StatisticsServiceImpl.class);
    private static final String DAO_EXCEPTION_OCCURRED_MESSAGE
            = "Dao exception occurred";

    private final Mapper<Dish, DishDto> dishMapper
            = DishMapper.getInstance();
    private final TransactionFactory transactionFactory;

    public StatisticsServiceImpl(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    @Override
    public long earnedLastMonth() throws ServiceException {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        LocalDate fromLocalDate = getFirstDayOfMonth(previousMonth);
        LocalDate toLocalDate = getLastDayOfMonth(previousMonth);
        Date from = Date.valueOf(fromLocalDate);
        Date to = Date.valueOf(toLocalDate);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            return orderDao.findSumOfCompletedOrderWhereActualRetrieveDateBetween(from, to);
        } catch (DaoException e) {
            throw new ServiceException(DAO_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    @Override
    public long earnedThisMonth() throws ServiceException {
        LocalDate nowLocalDate = LocalDate.now();
        LocalDate fromLocalDate = getFirstDayOfMonth(nowLocalDate);
        LocalDate toLocalDate = getLastDayOfMonth(nowLocalDate);
        Date from = Date.valueOf(fromLocalDate);
        Date to = Date.valueOf(toLocalDate);
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            return orderDao.findSumOfCompletedOrderWhereActualRetrieveDateBetween(from, to);
        } catch (DaoException e) {
            throw new ServiceException(DAO_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    @Override
    public long earnedTotal() throws ServiceException {
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderDao orderDao = transaction.createDao(OrderDao.class);
            return orderDao.findSumOfCompletedOrders();
        } catch (DaoException e) {
            throw new ServiceException(DAO_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    @Override
    public Map<DishDto, Entry<Long, Long>> findTopDishes(long limit)
            throws ServiceException {
        if (limit < 1) {
            throw new ServiceException("limit is invalid");
        }
        try (Transaction transaction = transactionFactory.createTransaction()) {
            OrderedDishDao orderedDishDao
                    = transaction.createDao(OrderedDishDao.class);
            DishDao dishDao = transaction.createDao(DishDao.class);
            List<Long> dishesId = orderedDishDao.findTopDishesId(limit);
            Map<DishDto, Entry<Long, Long>> resultMap = new LinkedHashMap<>();
            for (Long dishId : dishesId) {
                Long totalPrice = orderedDishDao.findTotalPriceByDishId(dishId);
                Long totalCount = orderedDishDao.findTotalCountByDishId(dishId);
                Entry<Long, Long> entry = new SimpleEntry<>(totalPrice, totalCount);
                dishDao.findById(dishId)
                        .map(dishMapper::mapEntityToDto)
                        .ifPresent(dishDto -> resultMap.put(dishDto, entry));
            }
            log.debug("Result map: {}", resultMap);
            return resultMap;
        } catch (DaoException e) {
            throw new ServiceException(DAO_EXCEPTION_OCCURRED_MESSAGE, e);
        }
    }

    @Override
    public LocalDate getFirstDayOfMonth(LocalDate date) throws ServiceException {
        log.debug("Received date: {}", date);
        if (date == null) {
            throw new ServiceException("Date is invalid");
        }
        LocalDate result = date.with(firstDayOfMonth());
        log.debug("Result date: {}", result);
        return result;
    }

    @Override
    public LocalDate getLastDayOfMonth(LocalDate date) throws ServiceException {
        log.debug("Received date: {}", date);
        if (date == null) {
            throw new ServiceException("Date is invalid");
        }
        LocalDate result = date.with(lastDayOfMonth());
        log.debug("Result date: {}", result);
        return result;
    }
}
