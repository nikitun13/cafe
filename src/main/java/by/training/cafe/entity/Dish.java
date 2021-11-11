package by.training.cafe.entity;

import java.util.Objects;

/**
 * The class {@code Dish} is an entity class that
 * represents {@code dish} table in database.
 *
 * @author Nikita Romanov
 */
public class Dish implements Entity {

    private Long id;
    private String name;
    private DishCategory category;
    private Long price;
    private String description;

    public Dish() {
    }

    public Dish(Long id, String name, DishCategory category,
                Long price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }

    public static DishBuilder builder() {
        return new DishBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public DishCategory getCategory() {
        return this.category;
    }

    public Long getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(DishCategory category) {
        this.category = category;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id)
                && Objects.equals(name, dish.name)
                && category == dish.category
                && Objects.equals(price, dish.price)
                && Objects.equals(description, dish.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price, description);
    }

    @Override
    public String toString() {
        return "Dish{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", category=" + category
                + ", price=" + price
                + ", description='" + description + '\''
                + '}';
    }

    public static class DishBuilder {

        private Long id;
        private String name;
        private DishCategory category;
        private Long price;
        private String description;

        DishBuilder() {
        }

        public DishBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DishBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DishBuilder category(DishCategory category) {
            this.category = category;
            return this;
        }

        public DishBuilder price(Long price) {
            this.price = price;
            return this;
        }

        public DishBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Dish build() {
            return new Dish(id, name, category, price, description);
        }
    }
}
