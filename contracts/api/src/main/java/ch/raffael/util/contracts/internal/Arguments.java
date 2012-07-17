package ch.raffael.util.contracts.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Arguments {

    String[] methods();

}
