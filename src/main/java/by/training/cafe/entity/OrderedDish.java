package by.training.cafe.entity;

import java.util.Objects;

/**
 * The class {@code OrderedDish} is an entity class that
 * represents {@code dish_orders} table in database.
 *
 * @author Nikita Romanov
 */
public class OrderedDish {

    private Order order;
    private Dish dish;
    private Long dishPrice;
    private Short dishCount;

    public OrderedDish() {
    }

    public OrderedDish(Order order, Dish dish, Long dishPrice, Short dishCount) {
        this.order = order;
        this.dish = dish;
        this.dishPrice = dishPrice;
        this.dishCount = dishCount;
    }

    public static OrderedDishBuilder builder() {
        return new OrderedDishBuilder();
    }

    public Long getTotalPrice() {
        return dishPrice * dishCount;
    }

    public Order getOrder() {
        return this.order;
    }

    public Dish getDish() {
        return this.dish;
    }

    public Long getDishPrice() {
        return this.dishPrice;
    }

    public Short getDishCount() {
        return this.dishCount;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public void setDishPrice(Long dishPrice) {
        this.dishPrice = dishPrice;
    }

    public void setDishCount(Short dishCount) {
        this.dishCount = dishCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedDish that = (OrderedDish) o;
        return Objects.equals(order, that.order)
                && Objects.equals(dish, that.dish)
                && Objects.equals(dishPrice, that.dishPrice)
                && Objects.equals(dishCount, that.dishCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, dish, dishPrice, dishCount);
    }

    @Override
    public String toString() {
        return "OrderedDish{"
                + "order=" + order
                + ", dish=" + dish
                + ", dishPrice=" + dishPrice
                + ", dishCount=" + dishCount
                + '}';
    }

    public static class OrderedDishBuilder {

        private Order order;
        private Dish dish;
        private Long dishPrice;
        private Short dishCount;

        OrderedDishBuilder() {
        }

        public OrderedDishBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public OrderedDishBuilder dish(Dish dish) {
            this.dish = dish;
            return this;
        }

        public OrderedDishBuilder dishPrice(Long price) {
            this.dishPrice = price;
            return this;
        }

        public OrderedDishBuilder dishCount(Short count) {
            this.dishCount = count;
            return this;
        }

        public OrderedDish build() {
            return new OrderedDish(order, dish, dishPrice, dishCount);
        }
    }
}
