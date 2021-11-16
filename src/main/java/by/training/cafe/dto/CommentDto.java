package by.training.cafe.dto;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class CommentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private UserDto user;
    private DishDto dish;
    private Short rating;
    private String body;
    private Timestamp createdAt;

    public CommentDto(Long id, UserDto user, DishDto dish,
                      Short rating, String body, Timestamp createdAt) {
        this.id = id;
        this.user = user;
        this.dish = dish;
        this.rating = rating;
        this.body = body;
        this.createdAt = createdAt;
    }

    public CommentDto() {
    }

    public static CommentDtoBuilder builder() {
        return new CommentDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public UserDto getUser() {
        return this.user;
    }

    public DishDto getDish() {
        return this.dish;
    }

    public Short getRating() {
        return this.rating;
    }

    public String getBody() {
        return this.body;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public void setDish(DishDto dish) {
        this.dish = dish;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDto that = (CommentDto) o;
        return Objects.equals(id, that.id)
                && Objects.equals(user, that.user)
                && Objects.equals(dish, that.dish)
                && Objects.equals(rating, that.rating)
                && Objects.equals(body, that.body)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, dish, rating, body, createdAt);
    }

    @Override
    public String toString() {
        return "CommentDto{"
                + "id=" + id
                + ", user=" + user
                + ", dish=" + dish
                + ", rating=" + rating
                + ", body='" + body + '\''
                + ", createdAt=" + createdAt
                + '}';
    }

    public static class CommentDtoBuilder {

        private Long id;
        private UserDto user;
        private DishDto dish;
        private Short rating;
        private String body;
        private Timestamp createdAt;

        CommentDtoBuilder() {
        }

        public CommentDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CommentDtoBuilder user(UserDto user) {
            this.user = user;
            return this;
        }

        public CommentDtoBuilder dish(DishDto dish) {
            this.dish = dish;
            return this;
        }

        public CommentDtoBuilder rating(Short rating) {
            this.rating = rating;
            return this;
        }

        public CommentDtoBuilder body(String body) {
            this.body = body;
            return this;
        }

        public CommentDtoBuilder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommentDto build() {
            return new CommentDto(id, user, dish, rating, body, createdAt);
        }
    }
}
