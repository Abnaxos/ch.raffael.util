package ch.raffael.util.binding.convert;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Not implements Converter<Boolean, Boolean> {

    @Override
    public Boolean sourceToTarget(Boolean value) {
        if ( value == null ) {
            return value;
        }
        return !value;
    }

    @Override
    public Boolean targetToSource(Boolean value) {
        if ( value == null ) {
            return value;
        }
        return !value;
    }
}
