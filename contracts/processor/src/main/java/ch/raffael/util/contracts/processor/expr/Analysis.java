package ch.raffael.util.contracts.processor.expr;

import javassist.CtClass;
import javassist.NotFoundException;

import ch.raffael.util.contracts.NotNull;

import static ch.raffael.util.contracts.processor.util.Javassist.*;
import static javassist.Modifier.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Analysis {

    private Analysis() {
    }

    public static boolean findClass(
            @NotNull CELTree node,
            @NotNull CELContext context)
            throws NotFoundException
    {
        CtClass search = context.getTargetClass();
        // check nested classes first
        while ( search != null ) {
            // FIXME: search class hierarchy
            if ( node.getText().equals(innerAwareClassName(search)) ) {
                node.setData(search);
                return true;
            }
            else {
                CtClass[] inner = search.getNestedClasses();
                if ( inner != null ) {
                    for ( CtClass i : inner ) {
                        if ( node.getText().equals(innerAwareClassName(i)) ) {
                            node.setData(i);
                            return true;
                        }
                    }
                }
            }
            search = search.getDeclaringClass();
        }
        CtClass found;
        // try same package
        found = context.findClass(context.getPackage().qualify(node.getText()));
        if ( found != null && found.getDeclaringClass() == null ) {
            return true;
        }
        // try java.lang
        found = context.findClass(PackageRef.LANG.qualify(node.getText()));
        if ( found != null && isPublic(found.getModifiers()) && found.getDeclaringClass() == null ) {
            return true;
        }
        // try as fully qualified class name
        PackageRef pkg = new PackageRef(node.getText());
        node.setData(pkg);
        CELTree up = (CELTree)node.getParent();
        while ( up.getType() == CELLexer.ACCESS ) {
            found = context.findClass(pkg.qualify(up.getText()));
            if ( found != null && found.getDeclaringClass() == null ) { // don't accept nested classes at this point
                if ( isPublic(found.getModifiers()) || found.getPackageName().equals(context.getPackage().getName()) ) {
                    up.setData(found);
                    return true;
                }
            }
            pkg = pkg.append(up.getText());
            up.setData(pkg);
            up = (CELTree)up.getParent();
        }
        // give up
        return false;
    }

}
