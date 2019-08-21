package cn.com.cyber.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageCodeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCodeUtil.class);

    @Autowired
    private MessageSource messageSource;

    public String getMessage(Object code) {
        String message = "";
        try {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(code.toString(), null, locale);
        } catch (Exception e) {
            LOGGER.error("parse message error! ", e);
        }
        return message;
    }

    public String getMessage(Object code, Object[] params) {
        String message = "";
        try {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(String.valueOf(code), params, locale);
        } catch (Exception e) {
            LOGGER.error("parse message error! ", e);
        }
        return message;
    }
}
