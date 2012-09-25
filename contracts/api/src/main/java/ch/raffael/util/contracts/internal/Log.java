package ch.raffael.util.contracts.internal;



import ch.raffael.util.contracts.NotNull;
import ch.raffael.util.contracts.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class Log {

    private static final String LOGGER_NAME = "ch.raffael.util.contracts";

    private static final Log INSTANCE;
    static {
        boolean slf4j = false;
        try {
            Class.forName("org.slf4j.LoggerFactory");
            Class.forName("org.slf4j.impl.StaticLoggerBinder");
            slf4j = true;
        }
        catch ( ClassNotFoundException e ) {
            slf4j = false;
        }
        if ( slf4j ) {
            INSTANCE = new Log("Using SLF4J for logging") {
                private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LOGGER_NAME);
                @Override
                public boolean isDebugEnabled() {
                    return logger.isTraceEnabled();
                }
                @Override
                public void debug(@NotNull String msg, @Nullable Object... args) {
                    if ( logger.isTraceEnabled() ) {
                        logger.trace(msg(msg, args));
                    }
                }
                @Override
                public void info(@NotNull String msg, @Nullable Object... args) {
                    if ( logger.isInfoEnabled() ) {
                        logger.info(msg(msg, args));
                    }
                }
                @Override
                public void error(@NotNull String msg, @Nullable Throwable exception, @Nullable Object... args) {
                    if ( logger.isErrorEnabled() ) {
                        logger.error(msg(msg, args), exception);
                    }
                }
            };
        }
        else {
            INSTANCE = new Log("Using java.util.logging for logging") {
                private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LOGGER_NAME);
                @Override
                public boolean isDebugEnabled() {
                    return logger.isLoggable(java.util.logging.Level.FINEST);
                }
                @Override
                public void debug(@NotNull String msg, @Nullable Object... args) {
                    if ( logger.isLoggable(java.util.logging.Level.FINEST) ) {
                        logger.log(java.util.logging.Level.FINEST, msg(msg, args));
                    }
                }
                @Override
                public void info(@NotNull String msg, @Nullable Object... args) {
                    if ( logger.isLoggable(java.util.logging.Level.INFO) ) {
                        logger.log(java.util.logging.Level.INFO, msg(msg, args));
                    }
                }
                @Override
                public void error(@NotNull String msg, @Nullable Throwable exception, @Nullable Object... args) {
                    if ( logger.isLoggable(java.util.logging.Level.SEVERE) ) {
                        logger.log(java.util.logging.Level.SEVERE, msg(msg, args), exception);
                    }
                }
            };
        }
        INSTANCE.info(INSTANCE.infoMsg);
    }

    private final String infoMsg;

    private Log(String msg) {
        this.infoMsg = msg;
    }

    public static Log getInstance() {
        return INSTANCE;
    }

    public abstract boolean isDebugEnabled();

    public void debug(@NotNull String msg) {
        debug(msg, (Object[])null);
    }

    public abstract void debug(@NotNull String msg, @Nullable Object... args);

    public void info(@NotNull String msg) {
        info(msg, (Object[])null);
    }

    public abstract void info(@NotNull String msg, @Nullable Object... args);

    public void error(@NotNull String msg) {
        error(msg, null, (Object[])null);
    }

    public void error(@NotNull String msg, @Nullable Object... args) {
        error(msg, null, args);
    }

    public void error(@NotNull String msg, @Nullable Throwable exception) {
        error(msg, exception, (Object[])null);
    }

    public abstract void error(@NotNull String msg, @Nullable Throwable exception, @Nullable Object... args);

    private static String msg(@NotNull String msg, @Nullable Object[] args) {
        if ( args == null || args.length == 0 ) {
            return msg;
        }
        else {
            return String.format(msg, args);
        }
    }

}
