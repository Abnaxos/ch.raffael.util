package ch.raffael.util.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.common.GetCaller;
import ch.raffael.util.common.annotations.Utility;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Utility
public class LogUtil {

    private LogUtil() {
    }

    @NotNull
    public static Logger getLogger(@NotNull Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    @NotNull
    public static Logger getLogger(@NotNull Object obj) {
        return LoggerFactory.getLogger(obj.getClass());
    }

    @NotNull
    public static Logger getLogger() {
        return getLogger(GetCaller.getCallerClass(LogUtil.class));
    }

}
