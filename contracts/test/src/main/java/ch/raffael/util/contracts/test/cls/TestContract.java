package ch.raffael.util.contracts.test.cls;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestContract {

    public static void test(Object obj) {
        if ( obj == null ) {
            RuntimeException exception =  new NullPointerException("(TestClass.java:12): Returning null");
            StackTraceElement[] stackTrace = exception.getStackTrace();
            StackTraceElement[] newStackTrace = new StackTraceElement[stackTrace.length - 1];
            System.arraycopy(stackTrace, 1, newStackTrace, 0, newStackTrace.length);
            newStackTrace[0] = new StackTraceElement("ch.raffael.util.contracts.test.cls.TestClass", "notNull", "TestClass.java", 13);
            exception.setStackTrace(newStackTrace);
            throw exception;
        }
    }

}
