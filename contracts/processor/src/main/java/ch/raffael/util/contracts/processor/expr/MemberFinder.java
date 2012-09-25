package ch.raffael.util.contracts.processor.expr;

import java.util.Set;

import com.google.common.collect.Sets;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.NotFoundException;

import ch.raffael.util.contracts.NotNull;
import ch.raffael.util.contracts.Nullable;

import static javassist.Modifier.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class MemberFinder {

    private final CELContext context;
    private final Set<String> searchedClasses = Sets.newHashSet();
    private final Set<CtMember> found = Sets.newHashSet();
    private CtMember firstNonStatic = null;

    public MemberFinder(@NotNull CELContext context) {
        this.context = context;
    }

    @Nullable
    public CtField find(@NotNull MemberType type, @NotNull String name) {
        // first, find a non-static member of that name within the class hierarchy
        // (no interfaces)

        searchedClasses.clear();
        found.clear();
        return null; // NO-IMPL
    }

    //@Nullable
    //private CtMember findNonStaticInHierarchy(@NotNull MemberType type, @NotNull String name, @NotNull CtClass search) {
    //    CtMember member = type.get(name, search);
    //    if ( member == null && !CtClass) {
    //
    //    }
    //}

    private void findField(CtClass search, String name) {
        try {
            CtField field = search.getDeclaredField(name);
            boolean include = true;
            if ( !field.visibleFrom(context.getTargetClass()) ) {
                include = false;
            }
            else if ( search != context.getTargetClass() && isStatic(field.getModifiers()) ) {
                include = false;
            }
            if ( include ) {
                found.add(field);
            }
            if ( !search.getName().equals("java.lang.Object") ) {
                findField(search.getSuperclass(), name);
            }
        }
        catch ( NotFoundException e ) {
            // ignore
        }
    }

    public static enum MemberType {
        FIELD {
            @Override
            @Nullable
            public CtMember get(@NotNull String name, @NotNull CtClass from) {
                try {
                    return from.getDeclaredField(name);
                }
                catch ( NotFoundException e ) {
                    return null;
                }
            }
        },
        METHOD {
            @Override
            @Nullable
            public CtMember get(@NotNull String name, @NotNull CtClass from) {
                try {
                    return from.getDeclaredMethod(name);
                }
                catch ( NotFoundException e ) {
                    return null;
                }
            }
        };

        @Nullable
        public abstract CtMember get(@NotNull String name, @NotNull CtClass from);
    }

}
