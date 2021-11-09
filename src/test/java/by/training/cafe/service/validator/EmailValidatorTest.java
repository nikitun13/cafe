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
class EmailValidatorTest {

    private final EmailValidator validator = EmailValidator.getInstance();

    @ParameterizedTest
    @MethodSource("validDataForIsValid")
    void shouldReturnTrueForValidEmails(String email) {
        boolean actual = validator.isValid(email);

        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidDataForIsValid")
    void shouldReturnFalseForInvalidEmails(String email) {
        boolean actual = validator.isValid(email);

        assertThat(actual).isFalse();
    }

    public Stream<Arguments> validDataForIsValid() throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource("email/valid-emails.txt").toURI();
        Path path = Path.of(uri);
        return Files.lines(path)
                .map(Arguments::of);
    }

    public Stream<Arguments> invalidDataForIsValid() throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource("email/invalid-emails.txt").toURI();
        Path path = Path.of(uri);
        return Files.lines(path)
                .map(Arguments::of);
    }
}
