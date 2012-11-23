package ch.raffael.util.groovy

/**
 * A validator for maps. This is mainly useful in DSLs for named arguments. It allows
 * the check the presence and types of required arguments, optional arguments and unknown
 * arguments.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class MapValidator {

    final Map map

    private final Set keys

    MapValidator(Map map) {
        this.map = map
        keys = new HashSet(map.keySet())
    }

    static void validate(Map map, Closure closure) {
        new MapValidator(map).validate(closure)
    }

    void validate(Closure closure) {
        Groovy.prepare(closure, this, Closure.DELEGATE_FIRST).call(map)
        verify()
    }

    void required(Object key, Class... types) {
        keys.remove(key)
        def value = map.get(key)
        if ( value == null ) {
            throw new IllegalArgumentException("Value $key is required")
        }
        if ( types ) {
            for ( t in types ) {
                if ( t.isInstance(value) ) {
                    return
                }
            }
            throw new IllegalArgumentException("Value $key must be an instance of $types")
        }
    }

    void optional(Object key, Class... types) {
        keys.remove(key)
        if ( types ) {
            def value = map.get(key)
            if ( value != null ) {
                for ( t in types ) {
                    if ( t.isInstance(value) ) {
                        return
                    }
                }
                throw new IllegalArgumentException("Value $key must be an instance of $types")
            }
        }
    }

    void verify() {
        if ( keys ) {
            throw new IllegalArgumentException("Unknown values: $keys")
        }
    }

}
