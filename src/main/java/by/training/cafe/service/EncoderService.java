package by.training.cafe.service;

/**
 * The class {@code EncoderService} is a class that
 * implements {@link Service}.<br/>
 * Encodes string and checks if the given string
 * matches the given hash.<br/>
 * The Hash-function used is implementation dependent.
 *
 * @author Nikita Romanov
 * @see Service
 */
public interface EncoderService extends Service {

    /**
     * Encodes string.
     *
     * @param raw {@code raw} string to be encoded.
     * @return encoded {@code raw} string.
     */
    String encode(String raw) throws ServiceException;

    /**
     * Checks if the given {@code raw} string
     * matches to the given {@code encoded} string.
     *
     * @param raw     {@code raw} string to be checked.
     * @param encoded {@code encoded} string.
     * @return {@code true} if the {@code raw} string matches to
     * the {@code encoded} string.
     * @throws ServiceException if {@code raw} or {@code encoded}
     *                          string is invalid.
     */
    boolean matches(String raw, String encoded) throws ServiceException;
}
