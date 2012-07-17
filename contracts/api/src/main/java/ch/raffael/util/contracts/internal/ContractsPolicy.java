package ch.raffael.util.contracts.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ch.raffael.util.contracts.ContractViolation;
import ch.raffael.util.contracts.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class ContractsPolicy {

    public static final String ROOT_NAME = "*";

    private static final ContractsPolicy ROOT = new ContractsPolicy("");

    private static final Map<String, ContractsPolicy> POLICIES = new HashMap<String, ContractsPolicy>();

    private final String name;
    private final LinkedList<ContractsPolicy> children = new LinkedList<ContractsPolicy>();
    private volatile boolean enabled;

    private ContractsPolicy(@NotNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ContractsPolicy[" + name + "]";
    }

    @NotNull
    public static ContractsPolicy getPolicy(@NotNull String name) {
        ContractsPolicy policy;
        if ( name.equals(ROOT_NAME) ) {
            return ROOT;
        }
        synchronized ( POLICIES ) {
            policy = POLICIES.get(name);
            if ( policy != null ) {
                return policy;
            }
            if ( !ROOT_NAME.equals(name) ) {
                boolean firstChar = true;
                for ( int i = 0; i < name.length(); i++ ) {
                    char c = name.charAt(i);
                    if ( firstChar ) {
                        if ( !Character.isJavaIdentifierStart(c) ) {
                            throw new IllegalArgumentException("Illegal policy name: '" + name + "'");
                        }
                        firstChar = false;
                    }
                    else {
                        if ( c == '.' ) {
                            firstChar = true;
                        }
                        else if ( !Character.isJavaIdentifierPart(c) ) {
                            throw new IllegalArgumentException("Illegal policy name: '" + name + "'");
                        }
                    }
                }
                if ( firstChar ) {
                    throw new IllegalArgumentException("Illegal policy name: '" + name + "'");
                }
            }
            return getPolicy0(name);
        }
    }

    @NotNull
    private static ContractsPolicy getPolicy0(@NotNull String name) {
        ContractsPolicy policy;
        policy = POLICIES.get(name);
        if ( policy == null ) {
            policy = new ContractsPolicy(name);
            ContractsPolicy parent;
            int pos = name.lastIndexOf('.');
            if ( pos < 0 ) {
                parent = ROOT;
            }
            else {
                parent = getPolicy0(name.substring(0, pos));
            }
            parent.children.add(policy);
            POLICIES.put(name, policy);
        }
        return policy;
    }

    @NotNull
    public static ContractsPolicy getPolicy(@NotNull Class<?> clazz) {
        Class<?> outer = clazz;
        while ( outer.getEnclosingClass() != null ) {
            outer = outer.getEnclosingClass();
        }
        synchronized ( POLICIES ) {
            return getPolicy0(outer.getName());
        }
    }

    @NotNull
    public static ContractsPolicy getPolicy(@NotNull Package pkg) {
        synchronized ( POLICIES ) {
            return getPolicy0(pkg.getName());
        }
    }

    public boolean enabled() {
        return enabled;
    }

    public void enable() {
        synchronized ( POLICIES ) {
            enable0();
        }
    }

    private void enable0() {
        enabled = true;
        for ( ContractsPolicy child : children ) {
            child.enable0();
        }
    }

    public void disable() {
        synchronized ( POLICIES ) {
            disable0();
        }
    }

    private void disable0() {
        enabled = false;
        for ( ContractsPolicy child : children ) {
            child.disable0();
        }
    }

    public void violation(ContractViolation violation) {
        throw violation;
    }

}
