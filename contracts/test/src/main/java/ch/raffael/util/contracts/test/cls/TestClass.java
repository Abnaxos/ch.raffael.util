package ch.raffael.util.contracts.test.cls;

import ch.raffael.util.contracts.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestClass {

    private String var = "VAR";
    private static Object $1;
    private static Object test;

    @NotNull
    public synchronized Object notNull(Object obj) {
        System.out.println(new Runnable() {
            @Override
            public void run() {
            }
        });
        return obj;
    }

    public <T extends java.util.Date> T erasure(int arg) {
        return null;
    }

    public static class Inner {
        private int x;
        @NotNull
        public Object method(int obj) {
            return obj;
        }
        public static class Inner2 {
            Class foo = Inner.class;
        }
    }

    public static void test() {
        class Local {
            void Method(TestClass x, Inner foo) {
                System.out.println(x.var+foo.x);
            }
        }
    }

}
