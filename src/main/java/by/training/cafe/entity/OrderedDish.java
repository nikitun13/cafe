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
    private Long price;
    private Short number;

    public OrderedDish() {
    }

    public OrderedDish(Order order, Dish dish, Long price, Short number) {
        this.order = order;
        this.dish = dish;
        this.price = price;
        this.number = number;
    }

    public static OrderedDishBuilder builder() {
        return new OrderedDishBuilder();
    }

    public Long getTotalPrice() {
        return price * number;
    }

    public Order getOrder() {
        return this.order;
    }

    public Dish getDish() {
        return this.dish;
    }

    public Long getPrice() {
        return this.price;
    }

    public Short getNumber() {
        return this.number;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setNumber(Short number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedDish that = (OrderedDish) o;
        return Objects.equals(order, that.order)
                && Objects.equals(dish, that.dish)
                && Objects.equals(price, that.price)
                && Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, dish, price, number);
    }

    @Override
    public String toString() {
        return "OrderedDish{"
                + "order=" + order
                + ", dish=" + dish
                + ", price=" + price
                + ", number=" + number
                + '}';
    }

    public static class OrderedDishBuilder {

        private Order order;
        private Dish dish;
        private Long price;
        private Short number;

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

        public OrderedDishBuilder price(Long price) {
            this.price = price;
            return this;
        }

        public OrderedDishBuilder number(Short number) {
            this.number = number;
            return this;
        }

        public OrderedDish build() {
            return new OrderedDish(order, dish, price, number);
        }
    }
}
