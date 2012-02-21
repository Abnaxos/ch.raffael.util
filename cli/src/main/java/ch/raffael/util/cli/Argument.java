package ch.raffael.util.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {
    String name();
    String[] alias() default {};
    boolean required() default false;
    boolean requireName() default false;
    Mode mode() default Mode.DEFAULT;
    String doc() default "No description";
    enum Mode {
        DEFAULT, END, END_PREFER
    }
}
