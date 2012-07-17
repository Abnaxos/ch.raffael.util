package ch.raffael.util.contracts.processor;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

import ch.raffael.util.contracts.processor.model.ClassInfo;
import ch.raffael.util.contracts.processor.model.MethodInfo;

import static javassist.Modifier.*;
import static javassist.bytecode.Descriptor.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Scanner {

    private final ProcessingEnvironment env;
    private final CtClass ctClass;
    private final ClassInfo classInfo;
    private final Log log;

    private String sourceFile;

    public Scanner(ProcessingEnvironment env, CtClass ctClass) {
        this.env = env;
        this.ctClass = ctClass;
        classInfo = new ClassInfo(ctClass.getName());
        log = env.getLog(ctClass.getName());
    }

    public ClassInfo collectClassInfo() throws NotFoundException {
        for ( CtConstructor constructor : ctClass.getConstructors() ) {
            if ( !constructor.isConstructor() ) {
                classInfo.addMethod(MethodInfo.constructor(classInfo, descriptor(constructor)));
            }
        }
        for ( CtMethod method : ctClass.getDeclaredMethods() ) {
            if ( !isStatic(method.getModifiers()) && !isNative(method.getModifiers()) ) {
                classInfo.addMethod(new MethodInfo(classInfo, method.getName(), descriptor(method), method.getReturnType().getName()));
                //LocalVariableTable locals = method.getMethodInfo2().
            }
            else {
                // FIXME: check for contracts and report error if any; for now ...
            }
        }
        if ( ctClass.getClassFile().getSuperclass() != null && !ctClass.getClassFile().getSuperclass().equals("java.lang.Object") ) {
            env.getClassInfo(ctClass.getClassFile().getSuperclass());
        }
        for ( String iface : ctClass.getClassFile().getInterfaces() ) {
            env.getClassInfo(iface);
        }
        sourceFile = ctClass.getClassFile().getSourceFile();
        //if ( sourceFile == null ) {
        //    log.warn("No source file information found")
        //}
        return null;
    }

    private void inherit(ClassInfo from) {
        classInfo.addInvariants(from.getInvariants());
    }

    private static String descriptor(CtBehavior behavior) {
        return getParamDescriptor(behavior.getMethodInfo().getDescriptor());
    }

    //private boolean checkUnderContract() throws ClassNotFoundException {
    //    if ( ctClass.getAnnotation(Invariant.class) != null ) {
    //        return true;
    //    }
    //    for ( CtMethod method: ctClass.getm)
    //}

}
