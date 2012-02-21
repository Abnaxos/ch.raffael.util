package ch.raffael.util.common.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;

import com.google.common.base.Function;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class EnhancedLogger implements Logger {

    private final Logger delegate;
    private final Function<String, String> enhancer;

    public EnhancedLogger(Logger delegate, Function<String, String> enhancer) {
        this.delegate = delegate;
        this.enhancer = enhancer;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        if ( delegate.isTraceEnabled() ) {
            delegate.trace(enhancer.apply(msg));
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if ( delegate.isTraceEnabled() ) {
            delegate.trace(enhancer.apply(format), arg);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if ( delegate.isTraceEnabled() ) {
            delegate.trace(enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void trace(String format, Object[] argArray) {
        if ( delegate.isTraceEnabled() ) {
            delegate.trace(enhancer.apply(format), argArray);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if ( delegate.isTraceEnabled() ) {
            delegate.trace(enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return delegate.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if ( delegate.isTraceEnabled(marker) ) {
            delegate.trace(marker, enhancer.apply(msg));
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if ( delegate.isTraceEnabled(marker) ) {
            delegate.trace(marker, enhancer.apply(format), arg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if ( delegate.isTraceEnabled(marker) ) {
            delegate.trace(marker, enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object[] argArray) {
        if ( delegate.isTraceEnabled(marker) ) {
            delegate.trace(marker, enhancer.apply(format), argArray);
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if ( delegate.isTraceEnabled(marker) ) {
            delegate.trace(marker, enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if ( delegate.isDebugEnabled() ) {
            delegate.debug(enhancer.apply(msg));
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if ( delegate.isDebugEnabled() ) {
            delegate.debug(enhancer.apply(format), arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if ( delegate.isDebugEnabled() ) {
            delegate.debug(enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void debug(String format, Object[] argArray) {
        if ( delegate.isDebugEnabled() ) {
            delegate.debug(enhancer.apply(format), argArray);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if ( delegate.isDebugEnabled() ) {
            delegate.debug(enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return delegate.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if ( delegate.isDebugEnabled(marker) ) {
            delegate.debug(marker, enhancer.apply(msg));
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if ( delegate.isDebugEnabled(marker) ) {
            delegate.debug(marker, enhancer.apply(format), arg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if ( delegate.isDebugEnabled(marker) ) {
            delegate.debug(marker, enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object[] argArray) {
        if ( delegate.isDebugEnabled(marker) ) {
            delegate.debug(marker, enhancer.apply(format), argArray);
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if ( delegate.isDebugEnabled(marker) ) {
            delegate.debug(marker, enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        if ( delegate.isInfoEnabled() ) {
            delegate.info(enhancer.apply(msg));
        }
    }

    @Override
    public void info(String format, Object arg) {
        if ( delegate.isInfoEnabled() ) {
            delegate.info(enhancer.apply(format), arg);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if ( delegate.isInfoEnabled() ) {
            delegate.info(enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void info(String format, Object[] argArray) {
        if ( delegate.isInfoEnabled() ) {
            delegate.info(enhancer.apply(format), argArray);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if ( delegate.isInfoEnabled() ) {
            delegate.info(enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return delegate.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        if ( delegate.isInfoEnabled(marker) ) {
            delegate.info(marker, enhancer.apply(msg));
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if ( delegate.isInfoEnabled(marker) ) {
            delegate.info(marker, enhancer.apply(format), arg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if ( delegate.isInfoEnabled(marker) ) {
            delegate.info(marker, enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void info(Marker marker, String format, Object[] argArray) {
        if ( delegate.isInfoEnabled(marker) ) {
            delegate.info(marker, enhancer.apply(format), argArray);
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if ( delegate.isInfoEnabled(marker) ) {
            delegate.info(marker, enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        if ( delegate.isWarnEnabled() ) {
            delegate.warn(enhancer.apply(msg));
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if ( delegate.isWarnEnabled() ) {
            delegate.warn(enhancer.apply(format), arg);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if ( delegate.isWarnEnabled() ) {
            delegate.warn(enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void warn(String format, Object[] argArray) {
        if ( delegate.isWarnEnabled() ) {
            delegate.warn(enhancer.apply(format), argArray);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if ( delegate.isWarnEnabled() ) {
            delegate.warn(enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return delegate.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if ( delegate.isWarnEnabled(marker) ) {
            delegate.warn(marker, enhancer.apply(msg));
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if ( delegate.isWarnEnabled(marker) ) {
            delegate.warn(marker, enhancer.apply(format), arg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if ( delegate.isWarnEnabled(marker) ) {
            delegate.warn(marker, enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object[] argArray) {
        if ( delegate.isWarnEnabled(marker) ) {
            delegate.warn(marker, enhancer.apply(format), argArray);
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if ( delegate.isWarnEnabled(marker) ) {
            delegate.warn(marker, enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        if ( delegate.isErrorEnabled() ) {
            delegate.error(enhancer.apply(msg));
        }
    }

    @Override
    public void error(String format, Object arg) {
        if ( delegate.isErrorEnabled() ) {
            delegate.error(enhancer.apply(format), arg);
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if ( delegate.isErrorEnabled() ) {
            delegate.error(enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void error(String format, Object[] argArray) {
        if ( delegate.isErrorEnabled() ) {
            delegate.error(enhancer.apply(format), argArray);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if ( delegate.isErrorEnabled() ) {
            delegate.error(enhancer.apply(msg), t);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return delegate.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        if ( delegate.isErrorEnabled(marker) ) {
            delegate.error(marker, enhancer.apply(msg));
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if ( delegate.isErrorEnabled(marker) ) {
            delegate.error(marker, enhancer.apply(format), arg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if ( delegate.isErrorEnabled(marker) ) {
            delegate.error(marker, enhancer.apply(format), arg1, arg2);
        }
    }

    @Override
    public void error(Marker marker, String format, Object[] argArray) {
        if ( delegate.isErrorEnabled(marker) ) {
            delegate.error(marker, enhancer.apply(format), argArray);
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if ( delegate.isErrorEnabled(marker) ) {
            delegate.error(marker, enhancer.apply(msg), t);
        }
    }

}
