package by.training.cafe.service.impl;

import by.training.cafe.service.ServiceException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BCryptEncoderServiceImplTest {

    private final BCryptEncoderServiceImpl encoder = new BCryptEncoderServiceImpl();

    @Test
    @Tag("encode")
    void shouldEncodeSimilarPasswordWithDifferentSalt() throws ServiceException {
        String password = "qwerty123";

        String firstHash = encoder.encode(password);
        String secondHash = encoder.encode(password);

        assertThat(firstHash).isNotEqualTo(secondHash);
    }

    @ParameterizedTest
    @MethodSource("dataForMatches")
    @Tag("matches")
    void passwordShouldMatchToAnyItsHash(String raw, String hash) throws ServiceException {
        boolean actual = encoder.matches(raw, hash);

        assertThat(actual).isTrue();
    }

    @Test
    @Tag("matches")
    void passwordShouldNotToOtherHash() throws ServiceException {
        String raw = "OtherPass123";
        String hash = "$2a$10$0vLc57n0KyrIQhLHOXFqH.5bK.pCjYaDp7EzXrSHFa0v2LCl/y1/W";

        boolean actual = encoder.matches(raw, hash);

        assertThat(actual).isFalse();
    }

    public Stream<Arguments> dataForMatches() {
        return Stream.of(
                Arguments.of("qwerty123", "$2a$10$PkZ6vo1TUu7ULDjLRtTfqebpjcYa/R0Qc3jWllO.OJXw9eW5Gg996"),
                Arguments.of("qwerty123", "$2a$10$PN4zI00T8FpBamfts18eYeyA8oAn..bvMKLhVB6LVBvZDBEcyVJ7a"),
                Arguments.of("qwerty123", "$2a$10$m8dUMkOMDuybjzXXnfRjMeASijN8.Ext.Jkk5Sfk/y2aAXbNzDu8."),
                Arguments.of("qwerty123", "$2a$10$0vLc57n0KyrIQhLHOXFqH.5bK.pCjYaDp7EzXrSHFa0v2LCl/y1/W")
        );
    }
}
