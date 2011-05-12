package ch.raffael.util.binding;

import ch.raffael.util.binding.convert.Converter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractBinding<T> extends PresentationModelMember implements Binding<T> {

    public <NEXT, BIND extends PresentationModelMember & ChainedBinding<NEXT, T>> BIND append(BIND binding) {
        binding.setSource(this);
        return add(binding);
    }

    public BufferedBinding<T> buffer() {
        return append(new BufferedBinding<T>());
    }

    public <NEXT> ConverterBinding<NEXT, T> convert(Converter<T, NEXT> converter) {
        return append(new ConverterBinding<NEXT, T>(converter));
    }

}
