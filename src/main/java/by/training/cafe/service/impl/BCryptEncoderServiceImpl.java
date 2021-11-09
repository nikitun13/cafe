package by.training.cafe.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import by.training.cafe.service.EncoderService;
import by.training.cafe.service.ServiceException;
import by.training.cafe.service.validator.StringValidator;
import by.training.cafe.service.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class {@code BCryptEncoderServiceImpl} is a class
 * that implements {@link EncoderService}.<br/>
 * Encodes string using BCrypt hash-function.<br/>
 * Delegates encoding to {@link BCrypt}.
 *
 * @author Nikita Romanov
 * @see EncoderService
 * @see BCrypt
 */
public class BCryptEncoderServiceImpl implements EncoderService {

    private static final Logger log
            = LogManager.getLogger(BCryptEncoderServiceImpl.class);
    private static final int DEFAULT_COST = 10;

    private final Validator<String> stringValidator
            = StringValidator.getInstance();

    private int cost = DEFAULT_COST;

    public BCryptEncoderServiceImpl() {
    }

    public BCryptEncoderServiceImpl(int cost) {
        this.cost = cost;
    }

    @Override
    public String encode(String raw) throws ServiceException {
        log.debug("Received raw = {}", raw);
        if (!stringValidator.isValid(raw)) {
            throw new ServiceException("Raw string is invalid: " + raw);
        }
        String hash = BCrypt.withDefaults()
                .hashToString(cost, raw.toCharArray());
        log.debug("Result hash = {}", hash);
        return hash;
    }

    @Override
    public boolean matches(String raw, String encoded) throws ServiceException {
        log.debug("Received raw = {} and hash = {}", raw, encoded);
        if (!stringValidator.isValid(raw)
                || !stringValidator.isValid(encoded)) {
            throw new ServiceException(
                    "Raw or encoded string is invalid. Raw = %s and Encoded = %s"
                            .formatted(raw, encoded));
        }
        boolean result = BCrypt.verifyer()
                .verify(raw.toCharArray(), encoded)
                .verified;
        log.debug("Matches result: {}", result);
        return result;
    }
}
