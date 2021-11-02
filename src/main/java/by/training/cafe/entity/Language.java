package by.training.cafe.entity;

import java.util.Locale;

/**
 * The class {@code Language} is an enumeration
 * of the supported languages.<br/>
 * Contains the corresponding locale inside.
 *
 * @author Nikita Romanov
 * @see Locale
 */
public enum Language {

    EN(new Locale("en", "US")),
    RU(new Locale("ru", "RU")),
    DE(new Locale("de", "DE"));

    private final Locale locale;

    static {
        Locale.setDefault(EN.getLocale());
    }

    Language(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
