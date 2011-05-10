package ch.raffael.util.binding.validate.validators;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;

import ch.raffael.util.binding.validate.ValidationResult;
import ch.raffael.util.binding.validate.Validator;
import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UrlValidator implements Validator<String> {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    @Override
    public void validate(String value, ValidationResult result) {
        try {
            new URL(value);
        }
        catch ( MalformedURLException e ) {
            log.trace("Invalid URL: {}", value, e);
            result.addError(e.getLocalizedMessage());
        }
    }
}
