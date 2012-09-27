package ch.raffael.util.cli;


import spock.lang.FailsWith
import spock.lang.Specification

import static ch.raffael.util.cli.Argument.*
import static com.google.common.base.Suppliers.*
import static org.spockframework.util.Assert.*

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class ReflectionHandlerSpec extends Specification {

    def String output = ''
    def calls = []
    def cmdHandler

    def "Command without arguments"() {
      given:
        cmd "cmd"

      when:
        parse "cmd"

      then:
        calls == [[]]
    }

    def "Command with one string argument"() {
      given:
        cmd "cmd", arg(String, "str")

      when:
        parse "cmd foo"

      then:
        calls == [["foo"]]
    }

    def "Command with one int argument"() {
      given:
        cmd "cmd", arg(int, "int")

      when:
        parse "cmd 42"

      then:
        calls == [[42]]
    }

    def "Default value for primitives"() {
        cmd "cmd", arg(int, "int")

      when:
        parse "cmd"

      then:
        calls == [[0]]
    }

    def "Two arguments"() {
      given:
        cmd 'cmd', arg(String, 'str'), arg(int, "int")

      when:
        parse "cmd foo 42"
        parse "cmd int:23 bar"

      then:
        calls == [["foo", 42], ["bar", 23]]
    }

    @FailsWith(value=CmdLineSyntaxException)
    def "Named arguments have priority, allows re-ordering"() {
      given:
        cmd "cmd", arg(String, "str"), arg(int, "int")

      when:
        parse "cmd 42 str:foo"

      then:
        calls == [["foo", 42]]
    }

    def "Exception when required argument is missing"() {
        //given:
        //  cmd "cmd", arg(String, "str").required(), arg(int, "int")
        //
        // STRANGE BUG!
        // The above causes a VerifyError (Stack size too large) in combination with
        // thrown() in the then-clause starting with groovy 1.8.6 or newer (1.8.5 works
        // fine).
        // Moving this line to the when-clause fixes this.

      when:
        cmd "cmd", arg(String, "str").required(), arg(int, "int")
        parse "cmd int:42"

      then:
        thrown CmdLineSyntaxException
    }

    def "End argument consumes the rest of the line as-is"() {
      given:
        cmd "cmd", arg(int, "int"), arg(String, "rest").mode(Mode.END)

      when:
        parse "cmd 42 to the end of the line"

      then:
        calls == [[ 42, "to the end of the line" ]]
    }

    def "End argument preserves spaces, except trailing"() {
      given:
        cmd "cmd", arg(int, "int"), arg(String, "rest").mode(Mode.END)

      when:
        parse "cmd 42 to the end   of  the line "

      then:
        calls == [[ 42, "to the end   of  the line" ]]
    }

    def "Preferred end argument requires other arguments to be named"() {
      given:
        cmd "cmd", arg(int, "int"), arg(String, "rest").mode(Mode.END_PREFER)

      when:
        parse "cmd 42 to the end of the line"
        parse "cmd int:42 to the end of the line"
        parse "cmd to the end of the int:42 line"

        then:
        calls == [
                [0, "42 to the end of the line"],
                [42, "to the end of the line"],
                [0, "to the end of the int:42 line"]
        ]
    }

    def "Syntax exception on type mismatch"() {
        //given:
        //  cmd "cmd", arg(int, "int")
        //
        // STRANGE BUG!
        // The above causes a VerifyError (Stack size too large) in combination with
        // thrown() in the then-clause starting with groovy 1.8.6 or newer (1.8.5 works
        // fine).
        // Moving this line to the when-clause fixes this.

      when:
        cmd "cmd", arg(int, "int")
        parse "cmd noint"

      then:
        thrown CmdLineSyntaxException
    }

    def "Flags are treated as named arguments"() {
      given:
        cmd "cmd", arg(String, "str"), arg(boolean, "flag")

      when:
        parse "cmd flag foo"

      then:
        calls == [[ "foo", true ]]
    }

    @FailsWith(CmdLineSyntaxException)
    def "Flags are treated as named arguments, includes re-ordering"() {
      given:
        cmd "cmd", arg(boolean, "flag"), arg(String, "str")

      when:
        parse "cmd foo flag"

      then:
        calls == [[ "foo", true ]]
    }

    def "No flags if specified as requireName=true"() {
      given:
        cmd "cmd", arg(boolean, "flag").requireName(), arg(String, "str")

      when:
        parse "cmd flag flag:no"

      then:
        calls == [[ false, "flag" ]]
    }

    def "Flags override preferred end"() {
      given:
        cmd "cmd", arg(boolean, "flag"), arg(String, "str").mode(Mode.END_PREFER)

      when:
        parse "cmd flag to the end"

      then:
        calls == [[ true, "to the end" ]]
    }

    def "Syntax exception when too many values"() {
        //given:
        //  cmd "cmd", arg(String, "single")
        //
        // STRANGE BUG!
        // The above causes a VerifyError (Stack size too large) in combination with
        // thrown() in the then-clause starting with groovy 1.8.6 or newer (1.8.5 works
        // fine).
        // Moving this line to the when-clause fixes this.

      when:
        cmd "cmd", arg(String, "single")
        parse "cmd two values"

      then:
        thrown CmdLineSyntaxException
    }

    def "Single argument converted to array"() {
      given:
        cmd "cmd", arg(String[], "arr")

      when:
        parse "cmd singleValue"

      then:
        calls == [[ ["singleValue" ] as String[] ]]
    }

    def "Multiple values in one argument"() {
      given:
        cmd "cmd", arg(String[], "arr")

      when:
        parse "cmd bar,foo"
        parse "cmd foo, bar"

        then:
        calls == [
                [ ["bar", "foo"] as String[] ],
                [ ["foo", "bar"] as String[] ]
        ]
    }

    def "Append values when two or more arguments"() {
      given:
        cmd "cmd", arg(String[], "arr"), arg(int, "int")

      when:
        parse "cmd foo 42 arr:bar, foobar arr:more"

      then:
        calls == [[ ["foo", "bar", "foobar", "more"] as String[], 42 ]]
    }

    def "Re-order automatically when there's a list"() {
      given:
        cmd "cmd", arg(String, "single"), arg(String[], "multi")

      when:
        parse "cmd foo,bar foobar"

      then:
        calls == [[ "foobar", ["foo", "bar"] as String[] ]]
    }

    def "Lists cause syntax exception if applied to single-value arguments"() {
        //given:
        //  cmd "cmd", arg(String, "single")
        //
        // STRANGE BUG!
        // The above causes a VerifyError (Stack size too large) in combination with
        // thrown() in the then-clause starting with groovy 1.8.6 or newer (1.8.5 works
        // fine).
        // Moving this line to the when-clause fixes this.

      when:
        cmd "cmd", arg(String, "single")
        parse "cmd foo, bar"

      then:
        thrown CmdLineSyntaxException
    }

    def "Automatically append arguments if last argument is a list"() {
      given:
        cmd "cmd", arg(String, "single"), arg(String[], "multi")

      when:
        parse "cmd one two, three, four five six,seven eight"

      then:
        calls == [[ "one", ["two", "three", "four", "five", "six", "seven", "eight"] as String[] ]]
    }


    /*
     * Helpers
     */

    def parse(String command) {
        def method = cmdHandler.class.methods.find { m -> m.getAnnotation(Command) != null }
        notNull(method, "No method annotated with @Command found")
        def outStr = new StringWriter()
        def out = new PrintWriter(outStr)
        new CmdLineParser(command, out, new ReflectionHandler(ofInstance(cmdHandler), method, method)).parse()
        out.close()
        output += outStr
    }

    def cmd(String name, Arg... args) {
        def argCounter = 0
        def src = "new Object() { def calls; @ch.raffael.util.cli.Command(name=\"$name\") def cmd("
        args.each { arg ->
            if ( argCounter > 0 ) {
                src += ', '
            }
            src += "@ch.raffael.util.cli.Argument(name=\"${arg.name}\""
            if ( arg.alias ) {
                src += ',alias=[' + arg.alias.collect { n -> "\"$n\"" }.join(',') << ']'
            }
            if ( arg.required ) {
                src += ',required=true'
            }
            if ( arg.requireName ) {
                src += ',requireName=true'
            }
            if ( arg.mode != Mode.DEFAULT ) {
                src += ",mode=ch.raffael.util.cli.Argument.Mode.${arg.mode.name()}"
            }
            def typeName = ""
            def type = arg.type
            while ( type.array ) {
                typeName += "[]"
                type = type.componentType
            }
            typeName = type.name + typeName
            src += ") ${typeName} arg${argCounter++}"
        }
        src += ") { calls << ["
        if ( argCounter > 0 ) {
            src += (0..(argCounter-1)).collect { n -> "arg$n" }.join(',')
        }
        src += '] }}'
        print src
        cmdHandler = Eval.me(src)
        cmdHandler.calls = calls
    }

    def arg(Class<?> type, String name) {
        return new Arg(type:type, name:name)
    }

    static class Arg {
        def Class<?> type
        def String name
        def String[] alias
        def boolean required
        def boolean requireName
        def Argument.Mode mode = Mode.DEFAULT
        Arg alias(String... alias) {
            this.alias = alias
            return this
        }
        Arg required() {
            required = true
            return this
        }
        Arg requireName() {
            requireName = true
            return this
        }
        Arg mode(Mode mode) {
            this.mode = mode
            return this
        }
    }

}
