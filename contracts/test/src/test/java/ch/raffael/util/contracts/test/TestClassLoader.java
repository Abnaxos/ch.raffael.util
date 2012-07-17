package ch.raffael.util.contracts.test;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;
import javassist.CannotCompileException;

import ch.raffael.util.contracts.processor.ContractsInstrumenter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestClassLoader extends ClassLoader {

    private final ContractsInstrumenter instrumenter;

    public TestClassLoader(ClassLoader parent) {
        super(parent);
        this.instrumenter = new ContractsInstrumenter(this);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        System.out.println(name);
        Class loaded = findLoadedClass(name);
        if ( loaded != null ) {
            return loaded;
        }
        if ( name.startsWith("ch.raffael.util.contracts.test.cls.TestClass") ) {
            return findClass(name);
        }
        else {
            return super.loadClass(name, resolve);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String binary = name.replace('.', '/') + ".class";
        URL url = getParent().getResource(binary);
        if ( url == null ) {
            throw new ClassNotFoundException(name);
        }
        try {
            byte[] bytecode = Resources.toByteArray(url);
            bytecode = instrumenter.instrument(bytecode);
            return defineClass(name, bytecode, 0, bytecode.length);
        }
        catch ( IOException e ) {
            throw new ClassNotFoundException(name + ": " + e, e);
        }
        catch ( CannotCompileException e ) {
            throw new ClassNotFoundException(name + ": " + e, e);
        }
    }
}
