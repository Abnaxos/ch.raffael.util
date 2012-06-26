package ch.raffael.util.common

import spock.lang.Specification

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class LazySerializationSpec extends Specification {

    def "Re-initialize transient Lazy"() {
      given:
        def lazy = create(false)
        def prevValue = lazy.get()

      when:
        lazy = serialize(lazy)

      then:
        lazy.get() != prevValue
    }

    def "Non-transient Lazy values match"() {
      given:
        def lazy = create(true)
        def prevValue = lazy.get()

      when:
        lazy = serialize(lazy)

      then:
        lazy.get() == prevValue
    }

    private LazyUUID create(boolean serializeValue) {
        return new LazyUUID(serializeValue);
    }

    private LazyUUID serialize(LazyUUID lazy) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(lazy);
        objOut.close();
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        return input.readObject() as LazyUUID;
    }

}
