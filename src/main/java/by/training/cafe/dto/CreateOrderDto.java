package by.training.cafe.dto;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class CreateOrderDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UserDto user;
    private Timestamp createdAt;
    private Timestamp expectedRetrieveDate;
    private Long debitedPoints;
    private Long totalPrice;

    public CreateOrderDto(UserDto user, Timestamp createdAt,
                          Timestamp expectedRetrieveDate,
                          Long debitedPoints, Long totalPrice) {
        this.user = user;
        this.createdAt = createdAt;
        this.expectedRetrieveDate = expectedRetrieveDate;
        this.debitedPoints = debitedPoints;
        this.totalPrice = totalPrice;
    }

    public CreateOrderDto() {
    }

    public static CreateOrderDtoBuilder builder() {
        return new CreateOrderDtoBuilder();
    }

    public UserDto getUser() {
        return this.user;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Timestamp getExpectedRetrieveDate() {
        return this.expectedRetrieveDate;
    }

    public Long getDebitedPoints() {
        return this.debitedPoints;
    }

    public Long getTotalPrice() {
        return this.totalPrice;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpectedRetrieveDate(Timestamp expectedRetrieveDate) {
        this.expectedRetrieveDate = expectedRetrieveDate;
    }

    public void setDebitedPoints(Long debitedPoints) {
        this.debitedPoints = debitedPoints;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateOrderDto that = (CreateOrderDto) o;
        return Objects.equals(user, that.user)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(expectedRetrieveDate, that.expectedRetrieveDate)
                && Objects.equals(debitedPoints, that.debitedPoints)
                && Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, createdAt, expectedRetrieveDate,
                debitedPoints, totalPrice);
    }

    @Override
    public String toString() {
        return "CreateOrderDto{"
                + "user=" + user
                + ", createdAt=" + createdAt
                + ", expectedRetrieveDate=" + expectedRetrieveDate
                + ", debitedPoints=" + debitedPoints
                + ", totalPrice=" + totalPrice
                + '}';
    }

    public static class CreateOrderDtoBuilder {

        private UserDto user;
        private Timestamp createdAt;
        private Timestamp expectedRetrieveDate;
        private Long debitedPoints;
        private Long totalPrice;

        CreateOrderDtoBuilder() {
        }

        public CreateOrderDtoBuilder user(UserDto user) {
            this.user = user;
            return this;
        }

        public CreateOrderDtoBuilder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CreateOrderDtoBuilder expectedRetrieveDate(
                Timestamp expectedRetrieveDate) {
            this.expectedRetrieveDate = expectedRetrieveDate;
            return this;
        }

        public CreateOrderDtoBuilder debitedPoints(Long debitedPoints) {
            this.debitedPoints = debitedPoints;
            return this;
        }

        public CreateOrderDtoBuilder totalPrice(Long totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public CreateOrderDto build() {
            return new CreateOrderDto(user, createdAt, expectedRetrieveDate,
                    debitedPoints, totalPrice);
        }
    }
}
