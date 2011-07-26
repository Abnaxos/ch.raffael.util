package ch.raffael.util.binding.validate.validators;

import java.util.Iterator;

import org.slf4j.Logger;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.net.InternetDomainName;

import ch.raffael.util.binding.validate.ValidationResult;
import ch.raffael.util.common.logging.LogUtil;

import static com.google.common.base.CharMatcher.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class EMailValidator extends AbstractValidator<String> {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();
    private static final CharMatcher localNameMatcher =
            inRange('a', 'z')
                    .or(inRange('A', 'Z'))
                    .or(inRange('0', '9'))
                    .or(anyOf(".!#$%&'*+-/=?^_`{|}~"));
    private static final CharMatcher qLocalNameMatcher =
            inRange((char)32, (char)126)
                    .and(noneOf("\"\\"));

    @SuppressWarnings( { "UnnecessaryReturnStatement" })
    @Override
    public void validate(String value, ValidationResult result) {
        if ( value.isEmpty() ) {
            return;
        }
        Iterator<String> iter = Splitter.on('@').limit(2).split(value).iterator();
        if ( !iter.hasNext() ) {
            invalid(result);
            return;
        }
        String localName = iter.next();
        if ( !iter.hasNext() ) {
            invalid(result);
            return;
        }
        String domain = iter.next();
        // check the local name
        if ( localName.isEmpty() ) {
            invalid(result);
            return;
        }
        if ( localName.charAt(0) == '"' ) {
            if ( localName.charAt(localName.length() - 1) != '"' ) {
                invalid(result);
                return;
            }
            if ( !qLocalNameMatcher.matchesAllOf(localName.substring(1, localName.length() - 1)) ) {
                invalid(result);
                return;
            }
        }
        else {
            if ( localName.charAt(0) == '.' || localName.charAt(localName.length() - 1) == '.' ) {
                invalid(result);
                return;
            }
            if ( localName.contains("..") ) {
                invalid(result);
                return;
            }
            if ( !localNameMatcher.matchesAllOf(localName) ) {
                invalid(result);
                return;
            }
        }
        // check the domain name
        InternetDomainName internetDomainName;
        try {
            internetDomainName = InternetDomainName.fromLenient(domain);
        }
        catch ( IllegalArgumentException e ) {
            invalid(result);
            return;
        }
        if ( !internetDomainName.isUnderPublicSuffix() ) {
            invalid(result);
            return;
        }
    }

    private void invalid(ValidationResult result) {
        result.addError("Invalid E-Mail address");
    }

}
