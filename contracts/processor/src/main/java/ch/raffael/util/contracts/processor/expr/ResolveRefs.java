package ch.raffael.util.contracts.processor.expr;

import java.util.Set;

import com.google.common.collect.Sets;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import ch.raffael.util.contracts.NotNull;
import ch.raffael.util.contracts.processor.util.ClassHierarchyVisitor;
import ch.raffael.util.contracts.processor.util.Javassist;

import static java.lang.reflect.Modifier.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ResolveRefs extends CELVisitorAction {

    private final CELContext context;

    public ResolveRefs(@NotNull CELContext context) {
        this.context = context;
    }

    @Override
    @NotNull
    protected CELTree pre(@NotNull final CELTree tree) {
        if ( tree.getType() == CELLexer.REF ) {
            try {
                // first, look for a non-static field within the hierarchy
                Javassist.visitHierarchy(context.getTargetClass(), Javassist.VisitMode.CLASS_ONLY, new ClassHierarchyVisitor() {
                    @Override
                    public boolean visit(@NotNull CtClass ctClass) {
                        try {
                            CtField field = ctClass.getField(tree.getText());
                            if ( !isStatic(field.getModifiers()) && field.visibleFrom(context.getTargetClass()) ) {
                                tree.setData(field);
                                return false;
                            }
                            else {
                                return true;
                            }

                        }
                        catch ( NotFoundException e ) {
                            return true;
                        }
                    }
                });
                if ( tree.getData() == null ) {
                    // Try to look for static fields
                    final Set<CtField> fields = Sets.newHashSet();
                    Javassist.visitHierarchyToOuter(context.getTargetClass(), Javassist.VisitMode.MIXED, new ClassHierarchyVisitor() {
                        @Override
                        public boolean visit(CtClass ctClass) {
                            try {
                                CtField field = ctClass.getField(tree.getText());
                                if ( isStatic(field.getModifiers()) && field.visibleFrom(context.getTargetClass()) ) {
                                    fields.add(field);
                                }
                                return true;
                            }
                            catch ( NotFoundException e ) {
                                return true;
                            }
                        }
                    });
                    if ( fields.size() == 1 ) {
                        tree.setData(fields.iterator().next());
                    }
                    else if ( fields.size() > 1 ) {
                        StringBuilder buf = new StringBuilder("Ambiguous field reference: " + tree.getText()+"; candidates:");
                        for ( CtField f : fields ) {
                            buf.append("\n    ").append(f);
                        }
                        context.reportProblem(new Problem(tree.getToken(), buf.toString()));
                        return tree;
                    }
                }
                if ( tree.getData() == null ) {
                    // OK, look for a class
                    if ( !Analysis.findClass(tree, context) ) {
                        context.reportProblem(new Problem(tree.getToken(), "No such field: " + tree.getText()));
                    }
                }
            }
            catch ( NotFoundException e ) {
                context.reportProblem(new Problem(tree.getToken(), e.toString()));
            }
        }
        return tree;
    }

    @Override
    @NotNull
    protected CELTree post(@NotNull CELTree tree) {
        return tree;
    }
}
