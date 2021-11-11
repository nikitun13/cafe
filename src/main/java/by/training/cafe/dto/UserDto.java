package by.training.cafe.dto;

import by.training.cafe.entity.UserRole;

import java.io.Serializable;
import java.util.Objects;

public class UserDto implements Serializable {

    private Long id;
    private String email;
    private UserRole role;
    private String firstName;
    private String lastName;
    private String phone;
    private Long points;
    private Boolean isBlocked;

    public UserDto() {
    }

    public UserDto(Long id, String email, UserRole role,
                   String firstName, String lastName, String phone,
                   Long points, Boolean isBlocked) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.points = points;
        this.isBlocked = isBlocked;
    }

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
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

    public Boolean isBlocked() {
        return this.isBlocked;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id)
                && Objects.equals(email, userDto.email)
                && role == userDto.role
                && Objects.equals(firstName, userDto.firstName)
                && Objects.equals(lastName, userDto.lastName)
                && Objects.equals(phone, userDto.phone)
                && Objects.equals(points, userDto.points)
                && Objects.equals(isBlocked, userDto.isBlocked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, role, firstName,
                lastName, phone, points, isBlocked);
    }

    @Override
    public String toString() {
        return "UserDto{"
                + "id=" + id
                + ", email='" + email + '\''
                + ", role='" + role + '\''
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", phone='" + phone + '\''
                + ", points=" + points
                + ", isBlocked=" + isBlocked
                + '}';
    }

    public static class UserDtoBuilder {

        private Long id;
        private String email;
        private UserRole role;
        private String firstName;
        private String lastName;
        private String phone;
        private Long points;
        private Boolean isBlocked;

        UserDtoBuilder() {
        }

        public UserDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDtoBuilder role(UserRole role) {
            this.role = role;
            return this;
        }

        public UserDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserDtoBuilder points(Long points) {
            this.points = points;
            return this;
        }

        public UserDtoBuilder isBlocked(Boolean isBlocked) {
            this.isBlocked = isBlocked;
            return this;
        }

        public UserDto build() {
            return new UserDto(id, email, role, firstName,
                    lastName, phone, points, isBlocked);
        }
    }
}
