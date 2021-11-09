package by.training.cafe.util;

public final class JspPathUtil {

    private static final String JSP_PATH_FORMAT
            = PropertiesUtil.get("jsp.basePath") + "%s.jsp";

    private JspPathUtil() {
    }

    public static String getPath(String jspName) {
        return String.format(JSP_PATH_FORMAT, jspName);
    }
}