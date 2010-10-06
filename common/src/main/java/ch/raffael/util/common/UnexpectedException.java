package ch.raffael.util.common;

import org.jetbrains.annotations.NotNull;


/**
 * Exception used to indicate that some unexpected exception has been caught. E.g.,
 * <code>new String(myByteArray, "UTF-8")</code> is declared to throw a
 * <code>UnsupportedEncodingException</code>, however, by specification, the charset
 * UTF-8 must be supported by any JVM.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UnexpectedException extends RuntimeException {

    public UnexpectedException(@NotNull Throwable cause) {
        super(cause);
    }

    public UnexpectedException(String message, @NotNull Throwable cause) {
        super((message == null ? cause.toString() : message), cause);
    }
}
