package ch.raffael.util.i18n;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ResourceBundle {

    @NotNull
    Meta meta();

    interface Meta {
        @NotNull
        Locale getLocale();

        @NotNull
        <T> Resource<T> resource(Class<T> type, String name);
        @NotNull
        <T> Resource<T> resource(Class<T> type, String name, Class<?>... paramTypes);
        @NotNull
        Resource<Object> resource(String name);
        @NotNull
        Resource<Object> resource(String name, Class<?>... paramTypes);
    }

    interface Resource<T> {
        @NotNull 
        T get();
        @NotNull
        T get(Object... args);
        @NotNull
        Class<?> type();
    }

}
