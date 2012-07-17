package ch.raffael.util.contracts.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ContractsInstrumenter {

    private final ClassLoader classLoader;
    private final ClassPool classPool;

    public ContractsInstrumenter(ClassLoader classLoader) {
        this.classLoader = classLoader;
        classPool = new ClassPool();
        classPool.appendClassPath(new LoaderClassPath(classLoader));
    }

    public byte[] instrument(byte[] bytecode) throws IOException, CannotCompileException {
        CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(bytecode));
        for ( CtMethod method : ctClass.getMethods() ) {
            CodeAttribute codeAttr = method.getMethodInfo().getCodeAttribute();
            if ( codeAttr != null ) {
                LocalVariableAttribute locals = (LocalVariableAttribute)codeAttr.getAttribute(LocalVariableAttribute.tag);
                if ( locals != null ) {
                    System.out.println(method);
                    for ( int i = 0; i < locals.tableLength(); i++ ) {
                        System.out.println("  " + i + ": " + locals.variableName(i));
                    }
                    method.insertBefore("System.out.println(42);\nthrow new RuntimeException();");
                    System.out.println(method + ": " + method.getMethodInfo2().getLineNumber(0));
                }

            }
            //if ( method.getName().equals("notNull") ) {
            //    //method.insertBefore("ch.raffael.util.contracts.test.cls.TestContract.test($1);");
            //    //method.insertAfter("ch.raffael.util.contracts.test.cls.TestContract.test($_);");
            //    System.out.println(method.getMethodInfo().getDescriptor());
            //}
        }
        return ctClass.toBytecode();
    }

}
