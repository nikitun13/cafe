package by.training.cafe.entity;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * The class {@code Comment} is an entity class that
 * represents {@code comment} table in database.
 *
 * @author Nikita Romanov
 */
public class Comment {

    private Long id;
    private User user;
    private Dish dish;
    private Short rating;
    private String body;
    private Timestamp createdAt;

    public Comment() {
    }

    public Comment(Long id, User user, Dish dish,
                   Short rating, String body, Timestamp createdAt) {
        this.id = id;
        this.user = user;
        this.dish = dish;
        this.rating = rating;
        this.body = body;
        this.createdAt = createdAt;
    }

    public static CommentBuilder builder() {
        return new CommentBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public Dish getDish() {
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setDish(Dish dish) {
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
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id)
                && Objects.equals(user, comment.user)
                && Objects.equals(dish, comment.dish)
                && Objects.equals(rating, comment.rating)
                && Objects.equals(body, comment.body)
                && Objects.equals(createdAt, comment.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, dish, rating, body, createdAt);
    }

    @Override
    public String toString() {
        return "Comment{"
                + "id=" + id
                + ", user=" + user
                + ", dish=" + dish
                + ", rating=" + rating
                + ", body='" + body + '\''
                + ", createdAt=" + createdAt
                + '}';
    }

    public static class CommentBuilder {

        private Long id;
        private User user;
        private Dish dish;
        private Short rating;
        private String body;
        private Timestamp createdAt;

        CommentBuilder() {
        }

        public CommentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CommentBuilder user(User user) {
            this.user = user;
            return this;
        }

        public CommentBuilder dish(Dish dish) {
            this.dish = dish;
            return this;
        }

        public CommentBuilder rating(Short rating) {
            this.rating = rating;
            return this;
        }

        public CommentBuilder body(String body) {
            this.body = body;
            return this;
        }

        public CommentBuilder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Comment build() {
            return new Comment(id, user, dish, rating, body, createdAt);
        }
    }
}
