package ch.raffael.util.binding.convert;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Converter<S, T> {

    T sourceToTarget(S value);

    S targetToSource(T value);

}
