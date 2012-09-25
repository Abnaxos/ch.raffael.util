package ch.raffael.util.contracts.processor.util;

import java.util.Set;

import com.google.common.collect.Sets;
import javassist.CtClass;
import javassist.NotFoundException;

import ch.raffael.util.contracts.NotNull;
import ch.raffael.util.contracts.Nullable;
import ch.raffael.util.contracts.Require;

import static ch.raffael.util.contracts.processor.util.Javassist.VisitMode.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Javassist {

    private Javassist() {
    }

    public static boolean isRootClass(@NotNull CtClass ctClass) {
        return ctClass.getName().equals("java.lang.Object");
    }

    public static void visitHierarchy(
            @Require({ "!ctClass.isAnnotation()", "!ctClass.isArray()", "!ctClass.isPrimitive()" })
            @NotNull CtClass ctClass,
            @NotNull VisitMode mode,
            @NotNull ClassHierarchyVisitor visitor)
            throws NotFoundException
    {
        Set<CtClass> visited =Sets.newHashSet();
        if ( mode == VisitMode.CLASS_FIRST ) {
            visitHierarchy(visited, ctClass, CLASS_ONLY, visitor);
            visitHierarchy(visited, ctClass, IFACE_ONLY, visitor);
        }
        else {
            visitHierarchy(visited, ctClass, mode, visitor);
        }
    }

    public static void visitHierarchyToOuter(
            @Require({ "!ctClass.isAnnotation()", "!ctClass.isArray()", "!ctClass.isPrimitive()" })
            @NotNull CtClass ctClass,
            @NotNull VisitMode mode,
            @NotNull ClassHierarchyVisitor visitor)
            throws NotFoundException
    {
        CtClass current = ctClass;
        while ( current != null ) {
            Set<CtClass> visited = Sets.newHashSet();
            if ( mode == VisitMode.CLASS_FIRST ) {
                visitHierarchy(visited, current, CLASS_ONLY, visitor);
                visitHierarchy(visited, current, IFACE_ONLY, visitor);
            }
            else {
                visitHierarchy(visited, current, mode, visitor);
            }
            current = current.getDeclaringClass();
        }
    }

    private static boolean visitHierarchy(
            @NotNull Set<CtClass> visited,
            @Require({ "!ctClass.isAnnotation()", "!ctClass.isArray()", "!ctClass.isPrimitive()" })
            @NotNull CtClass ctClass,
            @Require({ "mode!=CLASS_FIRST" })
            @NotNull VisitMode mode,
            @NotNull ClassHierarchyVisitor visitor)
            throws NotFoundException
    {
        visited.add(ctClass);
        if ( (!ctClass.isInterface() && mode != IFACE_ONLY) || (ctClass.isInterface() && mode != CLASS_ONLY) ) {
            if ( !visitor.visit(ctClass) ) {
                return false;
            }
        }
        if ( !ctClass.isInterface() && mode != VisitMode.IFACE_ONLY ) {
            if ( !visitor.visit(ctClass) ) {
                return false;
            }
        }
        if ( mode != CLASS_ONLY ) {
            CtClass[] ifaces = ctClass.getInterfaces();
            if ( ifaces != null ) {
                for ( CtClass iface : ifaces ) {
                    if ( visited.contains(iface) ) {
                        continue;
                    }
                    if ( !visitHierarchy(visited, iface, mode, visitor) ) {
                        return false;
                    }
                }
            }
        }
        if ( !isRootClass(ctClass) && !visited.contains(ctClass.getSuperclass()) ) {
            if ( !visitHierarchy(visited, ctClass.getSuperclass(), mode, visitor) ) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static String innerAwareClassName(@NotNull CtClass ctClass) throws NotFoundException {
        if ( ctClass.getEnclosingMethod() != null ) {
            // cannot access such classes
            return null;
        }
        CtClass outer = ctClass.getDeclaringClass();
        if ( outer == null ) {
            return ctClass.getSimpleName();
        }
        else {
            if ( !ctClass.getSimpleName().startsWith(outer.getSimpleName() + "$") ) {
                // FIXME: print some message or something, this shouldn't happen
                return null;
            }
            String name = ctClass.getSimpleName().substring(outer.getSimpleName().length() + 1);
            if ( name.isEmpty() || !Character.isJavaIdentifierStart(name.charAt(0)) ) {
                // isEmpty() would be very strange, otherwise I think it's safe to assume an anonymous class
                return null;
            }
            else {
                return name;
            }
        }
    }

    public static enum VisitMode {
        CLASS_ONLY, CLASS_FIRST, MIXED, IFACE_ONLY
    }

}
