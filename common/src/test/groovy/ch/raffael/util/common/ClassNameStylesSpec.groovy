package ch.raffael.util.common

import spock.lang.Specification

import static ch.raffael.util.common.Classes.NameStyle.*

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class ClassNameStylesSpec extends Specification {

    def "Natural and binary styles recognise primitives"() {
      when:
        def natural = NATURAL.forName(name)
        def binary = BINARY.forName(name)

      then:
        natural == clazz
        binary == clazz

      where:
        name   | clazz
        'int'  | int.class
        'void' | void.class
    }

    def "Natural style recognises [] for arrays"() {
      when:
        def c = NATURAL.forName(name)

      then:
        c == clazz

      where:
        name                 | clazz
        'java.lang.String[]' | String[].class
        'int[][]'            | int[][].class
    }

    def "Natural style recognises inner classes without use of dollar"() {
      when:
        def c = NATURAL.forName(name)

      then:
        c == clazz

      where:
        name                                                       | clazz
        'ch.raffael.util.common.ClassNameStylesSpec.MyInnerClass'   | MyInnerClass
        'ch.raffael.util.common.ClassNameStylesSpec.MyInnerClass[]' | MyInnerClass[].class
    }

    def "Binary style recognises arrays"() {
      when:
        def c = BINARY.forName(name)

      then:
        c == clazz

      where:
        name                                                          | clazz
        '[[I'                                                         | int[][].class
        '[Ljava.lang.String;'                                         | String[].class
        '[[[Lch.raffael.util.common.ClassNameStylesSpec$MyInnerClass;' | MyInnerClass[][][].class
    }

    def "Some nameFor() conversions"() {
      when:
        def n = style.nameFor(clazz)

      then:
        n == name

      where:
        style   | clazz          || name

        NATURAL | String.class   || 'java.lang.String'
        JVM     | String.class   || 'Ljava/lang/String;'

        NATURAL | String[].class || 'java.lang.String[]'

        NATURAL | int[][].class  || 'int[][]'
        BINARY  | int[][].class  || '[[I'

        BINARY  | int.class      || 'int'
        JVM     | int.class      || 'I'
    }

    def "NATURAL converts dollar-notation for inner classes back to dots"() {
      when:
        def n = NATURAL.nameFor(clazz)

      then:
        n == name

      where:
        clazz                  | name
        MyInnerClass           | 'ch.raffael.util.common.ClassNameStylesSpec.MyInnerClass'
        MyInnerClass[][].class | 'ch.raffael.util.common.ClassNameStylesSpec.MyInnerClass[][]'
    }

    static class MyInnerClass {
    }
    
}
