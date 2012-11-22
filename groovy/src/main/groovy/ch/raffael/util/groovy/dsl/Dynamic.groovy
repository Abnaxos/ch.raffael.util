package ch.raffael.util.groovy.dsl

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Use this method to dynamically resolve DSL methods. The method must take a string and
 * an object array as arguments. It should throw a MissingMethodException if it still
 * cannot resolve the method. Methods annotated with @Dynamic will be queried in the
 * reverse order of MetaClass#getMethods(), which will usually lead to methods of
 * subclasses being queried before the methods of superclasses.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Dynamic {

}
