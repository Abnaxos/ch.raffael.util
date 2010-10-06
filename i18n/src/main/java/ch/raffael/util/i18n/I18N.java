package ch.raffael.util.i18n;

import java.util.Locale;

import org.slf4j.Logger;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.common.NotImplementedException;
import ch.raffael.util.common.annotations.Utility;
import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Utility
public class I18N {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private static volatile boolean lenient = false;
    private static String locale;
    private static LocaleSearch localeSearch;

    private I18N() {
    }

    public synchronized static Locale getLocale() {
        return new Locale(locale);
    }

    public synchronized static void setLocale(Locale locale) {
        I18N.locale = locale.toString();
        localeSearch = new LocaleSearch(I18N.locale);
    }

    public synchronized LocaleSearch getLocaleSearch() {
        return localeSearch;
    }

    public static boolean isLenient() {
        return lenient;
    }

    public static void setLenient(boolean lenient) {
        I18N.lenient = lenient;
    }

    @NotNull
    public static <T extends ResourceBundle> T getBundle(Class<T> bundleClass) {
        throw new NotImplementedException(); // FIXME: not implemented
    }

}
