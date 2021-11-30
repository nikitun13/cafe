package by.training.cafe.controller.tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.time.Year;

public class CurrentYearTag extends TagSupport {

    private static final Logger log
            = LogManager.getLogger(CurrentYearTag.class);

    @Override
    public int doStartTag() {
        try {
            pageContext.getOut().write(Year.now().toString());
        } catch (IOException e) {
            log.error("IO exception occurred", e);
        }
        return SKIP_BODY;
    }
}
