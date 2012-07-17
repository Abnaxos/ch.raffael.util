package ch.raffael.util.contracts.test;


import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class NotNullSpec extends Specification {

    /**
     * I'm currently using this one to check some details out with the debugger.
     * Let it fail (or not).
     */
    @Ignore
    def "Various experimentsbv"() {
      given:
        def loader = new TestClassLoader(getClass().getClassLoader())

      when:
        //def cls2 = Class.forName("ch.raffael.util.contracts.test.cls.TestClass\$Inner", false, loader).newInstance();
        //println cls2
        def cls = Class.forName("ch.raffael.util.contracts.test.cls.TestClass", false, loader)
        cls.newInstance().notNull(null)

      then:
        true
        //def e = thrown(NullPointerException)
        //e.printStackTrace()
    }

    //def "@NotNull method does not throw NullPointerException when not returning null"() {
    //  given:
    //    def loader = new TestClassLoader(getClass().getClassLoader())
    //
    //  when:
    //    def cls = Class.forName("ch.raffael.util.contracts.test.cls.TestClass", false, loader)
    //    cls.newInstance().notNull("Foo")
    //
    //  then:
    //    true
    //}

}
