package ch.raffael.util.contracts.processor.expr;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class PackageRef {

    public static final PackageRef LANG = new PackageRef("java.lang");

    private final String name;

    public PackageRef(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PackageRef[" + name + "]";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !(o instanceof PackageRef) ) {
            return false;
        }
        PackageRef that = (PackageRef)o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public String qualify(String n) {
        return name + "." + n;
    }

    public PackageRef append(String n) {
        return new PackageRef(qualify(n));
    }

}
