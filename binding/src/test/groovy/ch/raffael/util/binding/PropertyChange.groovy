package ch.raffael.util.binding

import org.spockframework.lang.Wildcard

import java.beans.PropertyChangeEvent

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Category(PropertyChangeEvent)
class PropertyChange {

    static boolean matches(PropertyChangeEvent self, String propertyName, oldValue, newValue, source = Wildcard.INSTANCE ) {
        boolean result = self.propertyName == propertyName &&
                (oldValue instanceof Wildcard || self.oldValue == oldValue) &&
                (newValue instanceof Wildcard || self.newValue == newValue) &&
                (source instanceof Wildcard || self.source == source)
        //if ( !result ) {
        //    System.err.println "PropertyChangeEvent: $self.propertyName($self.oldValue->$self.newValue); expected $propertyName($oldValue->$newValue)"
        //}
        return result
    }

    static void print(PropertyChangeEvent self) {
        System.err.println "PropertyChange[${self.source}::${self.propertyName}(${self.oldValue}->${self.newValue}]"
    }

}
