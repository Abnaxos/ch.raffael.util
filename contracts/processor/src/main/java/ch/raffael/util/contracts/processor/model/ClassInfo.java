package ch.raffael.util.contracts.processor.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.Iterables;

import ch.raffael.util.common.collections.UMap;
import ch.raffael.util.common.collections.USet;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class ClassInfo {

    private final String name;

    private final USet<Condition<ClassInfo>> invariants = new USet<Condition<ClassInfo>>(new LinkedHashSet<Condition<ClassInfo>>());
    private final UMap<MethodIndicator, MethodInfo> methods = new UMap<MethodIndicator, MethodInfo>();

    public ClassInfo(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ClassInfo[" + name + "]";
    }

    public String getName() {
        return name;
    }

    public Set<Condition<ClassInfo>> getInvariants() {
        return invariants.unmodifiable();
    }

    public void addInvariant(Condition<ClassInfo> invariant) {
        invariants.add(invariant);
    }

    public void addInvariants(Iterable<Condition<ClassInfo>> invariants) {
        Iterables.addAll(this.invariants, invariants);
    }

    public Collection<MethodInfo> getMethods() {
        return methods.unmodifiable().values();
    }

    public void addMethod(MethodInfo methodInfo) {
        MethodIndicator indicator = new MethodIndicator(methodInfo);
        if ( methods.containsKey(indicator) ) {
            throw new IllegalStateException("Duplicate method: " + indicator);
        }

    }

    public MethodInfo getMethod(String name, String signature) {
        return methods.get(new MethodIndicator(name, signature));
    }

}
