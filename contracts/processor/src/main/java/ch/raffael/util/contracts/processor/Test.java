package ch.raffael.util.contracts.processor;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.bytecode.LocalVariableAttribute;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class Test {

    private static ClassPool pool = new ClassPool(true);

    private final int i;

    {
        i = 42;
    }

    public Test(String s) {
        System.out.println(s);
    }

    public void test(int i) {

    }

    public abstract void test(String s);

    static {
        //System.out.println("blubb");
    }

    public static void main(String[] args) throws Exception {
        CtClass ctClass = pool.get("ch.raffael.util.contracts.processor.Test");
        for ( CtConstructor c : ctClass.getConstructors() ) {
            System.out.println(c);
            System.out.println(c.getMethodInfo().getAttribute(LocalVariableAttribute.tag));
        }
        for ( CtMethod m : ctClass.getDeclaredMethods() ) {
            System.out.println(m);
            System.out.println(m.getMethodInfo().getAttribute(LocalVariableAttribute.tag));
            System.out.println(m.getMethodInfo().getCodeAttribute().getAttribute(LocalVariableAttribute.tag));
        }
    }

}
