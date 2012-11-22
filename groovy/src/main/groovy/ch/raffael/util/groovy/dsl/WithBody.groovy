package ch.raffael.util.groovy.dsl

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Mark a method as taking a closure. The return value of this method will be used as
 * delegate in the closure.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface WithBody {

    /**
     * Specify a method that invokes the closure. This method must take one closure as
     * argument. The closure will have been cloned and the delegate and resolve strategy
     * will be set correctly.
     *
     * @return
     */
    String invoker() default ""

}
