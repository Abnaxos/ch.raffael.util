package ch.raffael.util.contracts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>Specify a name for the parameter. This annotation allows to specify a name for a
 * parameter which differs from the declaration in the method. In most cases this isn't
 * really useful, as the processor extracts parameter names from the debug information.
 * However, unfortunately, this information is part of the code attribute (i.e. the
 * method body) and therefore not present in interfaces and abstract methods. There are
 * two ways around this:</p>
 *
 * <p><strong>Use <code>@param</code>:</strong>
 * <code>abstract void foo(@Require("@param > 0");</code></p>
 *
 * <p><strong>Use <code>@Named</code>:</strong>
 * <code>abstract void foo(@Named("min") int min, @Named("max") @Require("max>min") int max);</code>
 * </p>
 *
 * <p><strong>A mix of both:</strong>
 * <code>abstract void foo(@Named("min") int min, @Require("@param>min") int max);</code>
 * </p>
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.CLASS)
public @interface Named {

    String value();

}