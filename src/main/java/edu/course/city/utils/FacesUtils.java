package edu.course.city.utils;

import org.primefaces.util.MessageFactory;

import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;

public final class FacesUtils {

    private FacesUtils() {
        // Utils class
    }

    public static Locale getLocale() {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }

    public static ResourceBundle getBundle() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().getResourceBundle(context, "msg");
    }

    public static String getMessage(String messageKey, Object... params) {
        return MessageFactory.getFormattedText(getLocale(), getBundle().getString(messageKey), params);
    }
}
