package by.training.cafe.dto;

import java.io.Serializable;
import java.util.Objects;

public class OrderedDishDto implements Serializable {

    private OrderDto order;
    private DishDto dish;
    private Long dishPrice;
    private Short dishCount;
    private Long totalPrice;

    public OrderedDishDto() {
    }

    public OrderedDishDto(OrderDto order, DishDto dish, Long dishPrice,
                          Short dishCount, Long totalPrice) {
        this.order = order;
        this.dish = dish;
        this.dishPrice = dishPrice;
        this.dishCount = dishCount;
        this.totalPrice = totalPrice;
    }

    public static OrderedDishDtoBuilder builder() {
        return new OrderedDishDtoBuilder();
    }

    public OrderDto getOrder() {
        return this.order;
    }

    public DishDto getDish() {
        return this.dish;
    }

    public Long getDishPrice() {
        return this.dishPrice;
    }

    public Short getDishCount() {
        return this.dishCount;
    }

    public Long getTotalPrice() {
        return this.totalPrice;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public void setDish(DishDto dish) {
        this.dish = dish;
    }

    public void setDishPrice(Long dishPrice) {
        this.dishPrice = dishPrice;
    }

    public void setDishCount(Short dishCount) {
        this.dishCount = dishCount;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedDishDto that = (OrderedDishDto) o;
        return Objects.equals(order, that.order)
                && Objects.equals(dish, that.dish)
                && Objects.equals(dishPrice, that.dishPrice)
                && Objects.equals(dishCount, that.dishCount)
                && Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, dish, dishPrice, dishCount, totalPrice);
    }

    @Override
    public String toString() {
        return "OrderedDishDto{"
                + "order=" + order
                + ", dish=" + dish
                + ", dishPrice=" + dishPrice
                + ", dishCount=" + dishCount
                + ", totalPrice=" + totalPrice
                + '}';
    }

    public static class OrderedDishDtoBuilder {

        private OrderDto order;
        private DishDto dish;
        private Long dishPrice;
        private Short dishCount;
        private Long totalPrice;

        OrderedDishDtoBuilder() {
        }

        public OrderedDishDtoBuilder order(OrderDto order) {
            this.order = order;
            return this;
        }

        public OrderedDishDtoBuilder dish(DishDto dish) {
            this.dish = dish;
            return this;
        }

        public OrderedDishDtoBuilder dishPrice(Long dishPrice) {
            this.dishPrice = dishPrice;
            return this;
        }

        public OrderedDishDtoBuilder dishCount(Short dishCount) {
            this.dishCount = dishCount;
            return this;
        }

        public OrderedDishDtoBuilder totalPrice(Long totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public OrderedDishDto build() {
            return new OrderedDishDto(order, dish,
                    dishPrice, dishCount, totalPrice);
        }
    }
}
