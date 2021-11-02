package by.training.cafe.entity;

import java.util.Objects;

/**
 * The class {@code User} is an entity class that
 * represents {@code users} table in database.
 *
 * @author Nikita Romanov
 */
public class User {

    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
    private String phone;
    private Long points;
    private Boolean isBlocked;
    private Language language;

    public User() {
    }

    public User(Long id, String email, String password, UserRole role,
                String firstName, String lastName, String phone, Long points,
                Boolean isBlocked, Language language) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.points = points;
        this.isBlocked = isBlocked;
        this.language = language;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public UserRole getRole() {
        return this.role;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public Long getPoints() {
        return this.points;
    }

    public Boolean getIsBlocked() {
        return this.isBlocked;
    }

    public Language getLanguage() {
        return this.language;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(email, user.email)
                && Objects.equals(password, user.password)
                && role == user.role
                && Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName)
                && Objects.equals(phone, user.phone)
                && Objects.equals(points, user.points)
                && Objects.equals(isBlocked, user.isBlocked)
                && language == user.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, role, firstName,
                lastName, phone, points, isBlocked, language);
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", email='" + email + '\''
                + ", password='" + password + '\''
                + ", role=" + role
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", phone='" + phone + '\''
                + ", points=" + points
                + ", isBlocked=" + isBlocked
                + ", language=" + language
                + '}';
    }

    public static class UserBuilder {

        private Long id;
        private String email;
        private String password;
        private UserRole role;
        private String firstName;
        private String lastName;
        private String phone;
        private Long points;
        private Boolean isBlocked;
        private Language language;

        UserBuilder() {
        }

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder role(UserRole role) {
            this.role = role;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder points(Long points) {
            this.points = points;
            return this;
        }

        public UserBuilder isBlocked(Boolean isBlocked) {
            this.isBlocked = isBlocked;
            return this;
        }

        public UserBuilder language(Language language) {
            this.language = language;
            return this;
        }

        public User build() {
            return new User(id, email, password, role, firstName,
                    lastName, phone, points, isBlocked, language);
        }
    }
}
