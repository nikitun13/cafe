package by.training.cafe.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class DishDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String category;
    private Long price;
    private String description;

    public DishDto() {
    }

    public DishDto(Long id, String name, String category,
                   Long price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }

    public static DishDtoBuilder builder() {
        return new DishDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
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

    public void setCategory(String category) {
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
        DishDto dishDto = (DishDto) o;
        return Objects.equals(id, dishDto.id)
                && Objects.equals(name, dishDto.name)
                && Objects.equals(category, dishDto.category)
                && Objects.equals(price, dishDto.price)
                && Objects.equals(description, dishDto.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price, description);
    }

    @Override
    public String toString() {
        return "DishDto{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", category='" + category + '\''
                + ", price=" + price
                + ", description='" + description + '\''
                + '}';
    }

    public static class DishDtoBuilder {

        private Long id;
        private String name;
        private String category;
        private Long price;
        private String description;

        DishDtoBuilder() {
        }

        public DishDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DishDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DishDtoBuilder category(String category) {
            this.category = category;
            return this;
        }

        public DishDtoBuilder price(Long price) {
            this.price = price;
            return this;
        }

        public DishDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DishDto build() {
            return new DishDto(id, name, category, price, description);
        }
    }
}
