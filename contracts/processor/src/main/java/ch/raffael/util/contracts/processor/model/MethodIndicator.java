package ch.raffael.util.contracts.processor.model;

/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public final class MethodIndicator {

    private final String name;
    private final String signature;

    public MethodIndicator(String name, String signature) {
        this.name = name;
        this.signature = signature;
    }

    public MethodIndicator(MethodInfo methodInfo) {
        this(methodInfo.getName(), methodInfo.getSignature());
    }

    @Override
    public String toString() {
        return name + signature;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !(o instanceof MethodIndicator) ) {
            return false;
        }
        MethodIndicator that = (MethodIndicator)o;
        if ( !name.equals(that.name) ) {
            return false;
        }
        return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + signature.hashCode();
        return result;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }
}
