package ch.raffael.util.groovy.dsl

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Wrap the return value into a DslDelegate. If your method is annotated with
 * {@link WithBody @WithBody}, @Wrap is implied. In some (rare) cases, however, you might
 * want to wrap the return value even though you won't evaluate a body.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Wrap {

}
