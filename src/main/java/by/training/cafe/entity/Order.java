package by.training.cafe.entity;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * The class {@code Order} is an entity class that
 * represents {@code orders} table in database.
 *
 * @author Nikita Romanov
 */
public class Order implements Entity {

    private Long id;
    private User user;
    private Timestamp createdAt;
    private Timestamp expectedRetrieveDate;
    private Timestamp actualRetrieveDate;
    private OrderStatus status;
    private Long debitedPoints;
    private Long accruedPoints;
    private Long totalPrice;

    public Order() {
    }

    public Order(Long id, User user, Timestamp createdAt,
                 Timestamp expectedRetrieveDate,
                 Timestamp actualRetrieveDate, OrderStatus status,
                 Long debitedPoints, Long accruedPoints, Long totalPrice) {
        this.id = id;
        this.user = user;
        this.createdAt = createdAt;
        this.expectedRetrieveDate = expectedRetrieveDate;
        this.actualRetrieveDate = actualRetrieveDate;
        this.status = status;
        this.debitedPoints = debitedPoints;
        this.accruedPoints = accruedPoints;
        this.totalPrice = totalPrice;
    }

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Timestamp getExpectedRetrieveDate() {
        return this.expectedRetrieveDate;
    }

    public Timestamp getActualRetrieveDate() {
        return this.actualRetrieveDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public Long getDebitedPoints() {
        return this.debitedPoints;
    }

    public Long getAccruedPoints() {
        return this.accruedPoints;
    }

    public Long getTotalPrice() {
        return this.totalPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpectedRetrieveDate(Timestamp expectedRetrieveDate) {
        this.expectedRetrieveDate = expectedRetrieveDate;
    }

    public void setActualRetrieveDate(Timestamp actualRetrieveDate) {
        this.actualRetrieveDate = actualRetrieveDate;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setDebitedPoints(Long debitedPoints) {
        this.debitedPoints = debitedPoints;
    }

    public void setAccruedPoints(Long accruedPoints) {
        this.accruedPoints = accruedPoints;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id)
                && Objects.equals(user, order.user)
                && Objects.equals(createdAt, order.createdAt)
                && Objects.equals(expectedRetrieveDate, order.expectedRetrieveDate)
                && Objects.equals(actualRetrieveDate, order.actualRetrieveDate)
                && status == order.status
                && Objects.equals(debitedPoints, order.debitedPoints)
                && Objects.equals(accruedPoints, order.accruedPoints)
                && Objects.equals(totalPrice, order.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, createdAt, expectedRetrieveDate,
                actualRetrieveDate, status, debitedPoints,
                accruedPoints, totalPrice);
    }

    @Override
    public String toString() {
        return "Order{"
                + "id=" + id
                + ", user=" + user
                + ", createdAt=" + createdAt
                + ", expectedRetrieveDate=" + expectedRetrieveDate
                + ", actualRetrieveDate=" + actualRetrieveDate
                + ", status=" + status
                + ", debitedPoints=" + debitedPoints
                + ", accruedPoints=" + accruedPoints
                + ", totalPrice=" + totalPrice
                + '}';
    }

    public static class OrderBuilder {

        private Long id;
        private User user;
        private Timestamp createdAt;
        private Timestamp expectedRetrieveDate;
        private Timestamp actualRetrieveDate;
        private OrderStatus status;
        private Long debitedPoints;
        private Long accruedPoints;
        private Long totalPrice;

        OrderBuilder() {
        }

        public OrderBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public OrderBuilder user(User user) {
            this.user = user;
            return this;
        }

        public OrderBuilder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OrderBuilder expectedRetrieveDate(Timestamp expectedRetrieveDate) {
            this.expectedRetrieveDate = expectedRetrieveDate;
            return this;
        }

        public OrderBuilder actualRetrieveDate(Timestamp actualRetrieveDate) {
            this.actualRetrieveDate = actualRetrieveDate;
            return this;
        }

        public OrderBuilder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public OrderBuilder debitedPoints(Long debitedPoints) {
            this.debitedPoints = debitedPoints;
            return this;
        }

        public OrderBuilder accruedPoints(Long accruedPoints) {
            this.accruedPoints = accruedPoints;
            return this;
        }

        public OrderBuilder totalPrice(Long totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Order build() {
            return new Order(id, user, createdAt, expectedRetrieveDate,
                    actualRetrieveDate, status, debitedPoints,
                    accruedPoints, totalPrice);
        }
    }
}
