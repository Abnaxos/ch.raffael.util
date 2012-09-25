package ch.raffael.util.contracts.processor.util;

import javassist.CtClass;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ClassHierarchyVisitor {

    public boolean visit(CtClass ctClass);

}
