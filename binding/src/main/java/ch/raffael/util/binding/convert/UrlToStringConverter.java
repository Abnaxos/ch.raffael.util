package ch.raffael.util.binding.convert;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.net.InternetDomainName;
import org.jetbrains.annotations.NotNull;

import ch.raffael.util.binding.InvalidValueException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UrlToStringConverter implements Converter<URL, String> {

    private HostRequirements hostRequirements = HostRequirements.ANY_HOST;

    @Override
    public String sourceToTarget(URL value) {
        if ( value == null ) {
            return null;
        }
        else {
            return value.toString();
        }
    }

    @Override
    public URL targetToSource(String value) {
        if ( value == null || value.isEmpty() ) {
            return null;
        }
        try {
            return hostRequirements.checkHost(new URL(value));
        }
        catch ( MalformedURLException e ) {
            throw new InvalidValueException("Invalid URL '" + value + "': " + e.getLocalizedMessage(), e);
        }
    }

    @NotNull
    public HostRequirements getHostRequirements() {
        return hostRequirements;
    }

    public void setHostRequirements(@NotNull HostRequirements hostRequirements) {
        this.hostRequirements = hostRequirements;
    }

    @NotNull
    public UrlToStringConverter requireAnyHost() {
        setHostRequirements(HostRequirements.ANY_HOST);
        return this;
    }

    @NotNull
    public UrlToStringConverter requirePublicHost() {
        setHostRequirements(HostRequirements.PUBLIC_HOST);
        return this;
    }

    @NotNull
    public UrlToStringConverter optionalHost() {
        setHostRequirements(HostRequirements.OPTIONAL);
        return this;
    }

    public static enum HostRequirements {
        OPTIONAL {
            @NotNull
            @Override
            public URL checkHost(@NotNull URL url) {
                return url;
            }
        },
        ANY_HOST {
            @NotNull
            @Override
            public URL checkHost(@NotNull URL url) {
                if ( url.getHost() == null ) {
                    throw new InvalidValueException("Invalid URL " + url + ": Host name expected");
                }
                return url;
            }
        },
        PUBLIC_HOST {
            @NotNull
            @Override
            public URL checkHost(@NotNull URL url) {
                ANY_HOST.checkHost(url);
                try {
                    if ( !InternetDomainName.fromLenient(url.getHost()).isUnderPublicSuffix() ) {
                        throw new InvalidValueException("Invalid URL " + url + ": Public host name expected");
                    }
                }
                catch ( IllegalArgumentException e ) {
                    throw new InvalidValueException("Invalid URL " + url + ": " + e.getLocalizedMessage(), e);
                }
                return url;
            }
        };
        @NotNull
        public abstract URL checkHost(@NotNull URL url);
    }

}
