package by.training.cafe.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class OrderDto implements Serializable {

    private Long id;
    private UserDto user;
    private Timestamp createdAt;
    private Timestamp actualRetrieveDate;
    private Timestamp expectedRetrieveDate;
    private String status;
    private Long debitedPoints;
    private Long accruedPoints;
    private Long totalPrice;

    public OrderDto(Long id, UserDto user, Timestamp createdAt,
                    Timestamp actualRetrieveDate, Timestamp expectedRetrieveDate,
                    String status, Long debitedPoints,
                    Long accruedPoints, Long totalPrice) {
        this.id = id;
        this.user = user;
        this.createdAt = createdAt;
        this.actualRetrieveDate = actualRetrieveDate;
        this.expectedRetrieveDate = expectedRetrieveDate;
        this.status = status;
        this.debitedPoints = debitedPoints;
        this.accruedPoints = accruedPoints;
        this.totalPrice = totalPrice;
    }

    public OrderDto() {
    }

    public static OrderDtoBuilder builder() {
        return new OrderDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public UserDto getUser() {
        return this.user;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Timestamp getActualRetrieveDate() {
        return this.actualRetrieveDate;
    }

    public Timestamp getExpectedRetrieveDate() {
        return this.expectedRetrieveDate;
    }

    public String getStatus() {
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

    public void setUser(UserDto user) {
        this.user = user;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setActualRetrieveDate(Timestamp actualRetrieveDate) {
        this.actualRetrieveDate = actualRetrieveDate;
    }

    public void setExpectedRetrieveDate(Timestamp expectedRetrieveDate) {
        this.expectedRetrieveDate = expectedRetrieveDate;
    }

    public void setStatus(String status) {
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
        OrderDto orderDto = (OrderDto) o;
        return Objects.equals(id, orderDto.id)
                && Objects.equals(user, orderDto.user)
                && Objects.equals(createdAt, orderDto.createdAt)
                && Objects.equals(actualRetrieveDate, orderDto.actualRetrieveDate)
                && Objects.equals(expectedRetrieveDate, orderDto.expectedRetrieveDate)
                && Objects.equals(status, orderDto.status)
                && Objects.equals(debitedPoints, orderDto.debitedPoints)
                && Objects.equals(accruedPoints, orderDto.accruedPoints)
                && Objects.equals(totalPrice, orderDto.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, createdAt, actualRetrieveDate,
                expectedRetrieveDate, status, debitedPoints,
                accruedPoints, totalPrice);
    }

    @Override
    public String toString() {
        return "OrderDto{"
                + "id=" + id
                + ", user=" + user
                + ", createdAt=" + createdAt
                + ", actualRetrieveDate=" + actualRetrieveDate
                + ", expectedRetrieveDate=" + expectedRetrieveDate
                + ", status='" + status + '\''
                + ", debitedPoints=" + debitedPoints
                + ", accruedPoints=" + accruedPoints
                + ", totalPrice=" + totalPrice
                + '}';
    }

    public static class OrderDtoBuilder {

        private Long id;
        private UserDto user;
        private Timestamp createdAt;
        private Timestamp actualRetrieveDate;
        private Timestamp expectedRetrieveDate;
        private String status;
        private Long debitedPoints;
        private Long accruedPoints;
        private Long totalPrice;

        OrderDtoBuilder() {
        }

        public OrderDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public OrderDtoBuilder user(UserDto user) {
            this.user = user;
            return this;
        }

        public OrderDtoBuilder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OrderDtoBuilder actualRetrieveDate(Timestamp actualRetrieveDate) {
            this.actualRetrieveDate = actualRetrieveDate;
            return this;
        }

        public OrderDtoBuilder expectedRetrieveDate(Timestamp expectedRetrieveDate) {
            this.expectedRetrieveDate = expectedRetrieveDate;
            return this;
        }

        public OrderDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public OrderDtoBuilder debitedPoints(Long debitedPoints) {
            this.debitedPoints = debitedPoints;
            return this;
        }

        public OrderDtoBuilder accruedPoints(Long accruedPoints) {
            this.accruedPoints = accruedPoints;
            return this;
        }

        public OrderDtoBuilder totalPrice(Long totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public OrderDto build() {
            return new OrderDto(id, user, createdAt, actualRetrieveDate,
                    expectedRetrieveDate, status, debitedPoints,
                    accruedPoints, totalPrice);
        }
    }
}
