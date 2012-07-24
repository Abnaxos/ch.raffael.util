package ch.raffael.util.contracts.test.cls;

import ch.raffael.util.contracts.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestClass {

    protected String var = "VAR";
    private static Object $1;
    private static Object test;

    @NotNull
    public synchronized Object notNull(Object obj) {
        System.out.println(obj);
        return obj;
    }

    public <T extends java.util.Date> T erasure(int arg) {
        return null;
    }

    public class Inner {
        @NotNull
        public Object method(int obj) {
            return obj;
        }
        public class Inner2 {

        }
    }

}
