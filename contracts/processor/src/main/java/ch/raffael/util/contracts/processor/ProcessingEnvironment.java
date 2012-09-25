package ch.raffael.util.contracts.processor;

import java.util.Map;

import com.google.common.collect.Maps;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import ch.raffael.util.contracts.NotNull;
import ch.raffael.util.contracts.Nullable;
import ch.raffael.util.contracts.processor.model.ClassInfo;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ProcessingEnvironment {

    private final Map<String, ClassInfo> classInfos = Maps.newHashMap();

    private final ClassPool classPool;

    public ProcessingEnvironment(ClassPool classPool) {
        this.classPool = classPool;
    }

    public void getClassInfo(String className) throws NotFoundException {
        ClassInfo info = classInfos.get(className);
        //if ( info == null ) {
        //    classInfos.put(new Scanner(this, classPool.get(className)).collectClassInfo());
        //}
    }

    @NotNull
    public ClassPool getClassPool() {
        return classPool;
    }

    @Nullable
    public CtClass findClass(@NotNull String name) {
        try {
            return classPool.get(name);
        }
        catch ( NotFoundException e ) {
            return null;
        }
    }

    public Log getLog() {
        return null;
    }

    public Log getLog(String resource) {
        return null;
    }

}
