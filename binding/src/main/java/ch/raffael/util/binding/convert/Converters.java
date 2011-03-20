package ch.raffael.util.binding.convert;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Converters {

    private final static Converter<Object, Object> DO_NOTHING = new Converter<Object, Object>() {
        @Override
        public Object sourceToTarget(Object value) {
            return value;
        }
        @Override
        public Object targetToSource(Object value) {
            return value;
        }
    };

    @SuppressWarnings( { "unchecked" })
    @NotNull
    public static <T> Converter<T, T> doNothing() {
        return (Converter<T, T>)DO_NOTHING;
    }

    @NotNull
    public static <S, T> Converter<S, T> reverse(@NotNull Converter<T, S> converter) {
        return new Reverse<S, T>(converter);
    }

    @NotNull
    public static <S, M, T> ChainLink<S, M, T> chain(@NotNull Converter<S, M> c1, @NotNull Converter<M, T> c2) {
        return new ChainLink<S, M, T>(c1, c2);
    }

    @NotNull 
    public static <S, T> ChainLink<S, S, T> chain(@NotNull Converter<S, T> converter) {
        return new ChainLink<S, S, T>(Converters.<S>doNothing(), converter);
    }

    public static class ChainLink<S, M, T> implements Converter<S, T> {

        private final Converter<S, M> c1;
        private final Converter<M, T> c2;

        public ChainLink(@NotNull Converter<S, M> c1, @NotNull Converter<M, T> c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        public <NT> ChainLink<S, T, NT> append(@NotNull Converter<T, NT> converter) {
            return new ChainLink<S, T, NT>(this, converter);
        }

        @Override
        public T sourceToTarget(S value) {
            return c2.sourceToTarget(c1.sourceToTarget(value));
        }

        @Override
        public S targetToSource(T value) {
            return c1.targetToSource(c2.targetToSource(value));
        }
    }

}
