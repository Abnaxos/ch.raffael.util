package ch.raffael.util.contracts.test.cls;

import ch.raffael.util.contracts.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestClass {

    private String var = "VAR";

    @NotNull
    public synchronized Object notNull(Object obj) {
        System.out.println(obj);
        return obj;
    }

    public class Inner {
        @NotNull
        public Object method(Object obj) {
            return obj;
        }
        public class Inner2 {

        }
    }

}
