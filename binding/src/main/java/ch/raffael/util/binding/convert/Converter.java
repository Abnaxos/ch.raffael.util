package ch.raffael.util.binding.convert;

import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Converter<S, T> {

    T sourceToTarget(@Nullable S value);

    S targetToSource(@Nullable T value);

}
