package by.training.cafe.util;

public final class StringUtil {

    private StringUtil() {
    }

    public static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
