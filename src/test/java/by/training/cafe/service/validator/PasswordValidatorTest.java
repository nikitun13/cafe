package by.training.cafe.service.validator;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PasswordValidatorTest {

    private final PasswordValidator validator = PasswordValidator.getInstance();

    @ParameterizedTest
    @MethodSource("validDataForIsValid")
    void shouldReturnTrueForValidPasswords(String password) {
        boolean actual = validator.isValid(password);

        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidDataForIsValid")
    void shouldReturnFalseForInvalidPasswords(String password) {
        boolean actual = validator.isValid(password);

        assertThat(actual).isFalse();
    }

    public Stream<Arguments> validDataForIsValid() throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource("password/valid-passwords.txt").toURI();
        Path path = Path.of(uri);
        return Files.lines(path)
                .map(Arguments::of);
    }

    public Stream<Arguments> invalidDataForIsValid() throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource("password/invalid-passwords.txt").toURI();
        Path path = Path.of(uri);
        return Files.lines(path)
                .map(Arguments::of);
    }
}