package ch.raffael.util.contracts.processor.model;

import java.util.LinkedHashSet;
import java.util.Set;

import ch.raffael.util.common.collections.USet;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class ParameterInfo {

    private final MethodInfo declaringMethod;
    private final String type;

    private final USet<Condition<ParameterInfo>> preconditions = new USet<Condition<ParameterInfo>>(new LinkedHashSet<Condition<ParameterInfo>>());
    private Boolean notNull;

    public ParameterInfo(MethodInfo declaringMethod, String type) {
        this.declaringMethod = declaringMethod;
        this.type = type;
    }

    public MethodInfo getDeclaringMethod() {
        return declaringMethod;
    }

    public String getType() {
        return type;
    }

    public Set<Condition<ParameterInfo>> getPreconditions() {
        return preconditions.unmodifiable();
    }

    public void addPrecondition(Condition<ParameterInfo> condition) {
        preconditions.add(condition);
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }
}
