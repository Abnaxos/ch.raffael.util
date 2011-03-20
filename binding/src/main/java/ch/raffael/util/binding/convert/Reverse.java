package ch.raffael.util.binding.convert;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Reverse<S, T> implements Converter<S, T> {

    private final Converter<T, S> delegate;

    public Reverse(Converter<T, S> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T sourceToTarget(S value) {
        return delegate.targetToSource(value);
    }

    @Override
    public S targetToSource(T value) {
        return delegate.sourceToTarget(value);
    }
}
