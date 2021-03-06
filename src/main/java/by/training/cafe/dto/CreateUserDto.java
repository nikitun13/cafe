package by.training.cafe.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CreateUserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private String repeatPassword;
    private String firstName;
    private String lastName;
    private String phone;

    public CreateUserDto() {
    }

    public CreateUserDto(String email, String password, String repeatPassword,
                         String firstName, String lastName, String phone) {
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public static CreateUserDtoBuilder builder() {
        return new CreateUserDtoBuilder();
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateUserDto that = (CreateUserDto) o;
        return Objects.equals(email, that.email)
                && Objects.equals(password, that.password)
                && Objects.equals(repeatPassword, that.repeatPassword)
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, repeatPassword,
                firstName, lastName, phone);
    }

    @Override
    public String toString() {
        return "CreateUserDto{"
                + "email='" + email + '\''
                + ", password='" + password + '\''
                + ", repeatPassword='" + repeatPassword + '\''
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", phone='" + phone + '\''
                + '}';
    }

    public static class CreateUserDtoBuilder {

        private String email;
        private String password;
        private String repeatPassword;
        private String firstName;
        private String lastName;
        private String phone;

        CreateUserDtoBuilder() {
        }

        public CreateUserDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CreateUserDtoBuilder password(String password) {
            this.password = password;
            return this;
        }

        public CreateUserDtoBuilder repeatPassword(String repeatPassword) {
            this.repeatPassword = repeatPassword;
            return this;
        }

        public CreateUserDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public CreateUserDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public CreateUserDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public CreateUserDto build() {
            return new CreateUserDto(email, password, repeatPassword,
                    firstName, lastName, phone);
        }
    }
}
