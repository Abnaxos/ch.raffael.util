package ch.raffael.util.contracts.processor;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Log {

    void error(int line, String message, String... args);
    void warn(int line, String message, String... args);
    void info(int line, String message, String... args);
    void debug(int line, String message, String... args);

    void error(String message, String... args);
    void warn(String message, String... args);
    void info(String message, String... args);
    void debug(String message, String... args);

    Log forResource(String resource);

}
