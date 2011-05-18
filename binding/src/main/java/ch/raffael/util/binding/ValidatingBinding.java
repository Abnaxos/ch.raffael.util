package ch.raffael.util.binding;

import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ValidatingBinding<T> extends Binding<T>, Validator<T> {

}
