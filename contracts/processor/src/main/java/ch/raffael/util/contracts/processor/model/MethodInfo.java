package ch.raffael.util.contracts.processor.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;

import ch.raffael.util.common.collections.UList;
import ch.raffael.util.common.collections.UMap;
import ch.raffael.util.common.collections.USet;
import ch.raffael.util.contracts.processor.util.Types;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class MethodInfo {

    public static final String CONSTRUCTOR_NAME = "<init>";

    private final ClassInfo declaringClass;
    private final String name;
    private final String signature;
    private final MethodIndicator indicator;
    private final String returnType;

    private final UList<ParameterInfo> parameters = new UList<ParameterInfo>();
    private final UMap<String, Integer> parameterNameMap = new UMap<String, Integer>();

    private USet<Condition<MethodInfo>> preconditions = new USet<Condition<MethodInfo>>(new LinkedHashSet<Condition<MethodInfo>>());
    private USet<Condition<MethodInfo>> postconditions = new USet<Condition<MethodInfo>>(new LinkedHashSet<Condition<MethodInfo>>());
    private Boolean notNull;

    public MethodInfo(ClassInfo declaringClass, String name, String signature, String returnType) {
        this.declaringClass = declaringClass;
        this.name = name;
        this.signature = signature;
        this.indicator = new MethodIndicator(name, signature);
        this.returnType = returnType;
    }

    public MethodInfo(ClassInfo declaringClass, MethodIndicator indicator, String returnType) {
        this.declaringClass = declaringClass;
        this.name = indicator.getName();
        this.signature = indicator.getSignature();
        this.indicator = indicator;
        this.returnType = returnType;
    }

    public static MethodInfo constructor(ClassInfo declaringClass, String signature) {
        return new MethodInfo(declaringClass, CONSTRUCTOR_NAME, signature, Types.VOID);
    }

    @Override
    public String toString() {
        return "MethodInfo[" + getDeclaringClass().getName() + "::" + indicator + "]";
    }

    public ClassInfo getDeclaringClass() {
        return declaringClass;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public MethodIndicator getIndicator() {
        return indicator;
    }

    public boolean isConstructor() {
        return name.equals(CONSTRUCTOR_NAME);
    }

    public String getReturnType() {
        return returnType;
    }

    public List<ParameterInfo> getParameters() {
        return parameters.unmodifiable();
    }

    public void addParameter(ParameterInfo info) {
        parameters.add(info);
    }

    public void addParameter(ParameterInfo info, String name) {
        parameters.add(info);
        mapParameter(name, parameters.size() - 1);
    }

    public int getParameterIndex(String name) {
        Integer index = parameterNameMap.get(name);
        if ( index == null ) {
            return -1;
        }
        else {
            return index;
        }
    }

    public void mapParameter(String name, int index) {
        parameterNameMap.put(name, index);
    }

    public Set<Condition<MethodInfo>> getPreconditions() {
        return preconditions.unmodifiable();
    }

    public void addPrecondition(Condition<MethodInfo> condition) {
        preconditions.add(condition);
    }

    public void addPreconditions(Iterable<Condition<MethodInfo>> conditions) {
        Iterables.addAll(preconditions, conditions);
    }

    public Set<Condition<MethodInfo>> getPostconditions() {
        return postconditions.unmodifiable();
    }

    public void addPostcondition(Condition<MethodInfo> condition) {
        postconditions.add(condition);
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }
}
