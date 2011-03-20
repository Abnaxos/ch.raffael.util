package ch.raffael.util.binding;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ChainedBinding<T, S> extends Binding<T> {

    String PROPERTY_SOURCE = "source";

    Binding<S> getSource();

    void setSource(Binding<S> source);
}
