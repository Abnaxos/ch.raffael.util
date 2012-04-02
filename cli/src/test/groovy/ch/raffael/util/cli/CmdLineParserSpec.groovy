package ch.raffael.util.cli;


import org.codehaus.groovy.control.io.NullWriter
import spock.lang.Shared
import spock.lang.Specification

import static ch.raffael.util.cli.CmdLineHandler.Mode.*

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class CmdLineParserSpec extends Specification {

    @Shared
    def PrintWriter out = new PrintWriter(new NullWriter())

    def handler = Mock(CmdLineHandler)

    def "Basic command line parsing, all features"() {
      when:
        parse "module:command named:'value ''with'' escape' unnamed,list 'other unnamed' list:named,'the list', more"

      then:
        1 * handler.command(out, _, "module", "command") >> PARSE
        1 * handler.value(out, _, "named", "value 'with' escape") >> PARSE
        1 * handler.value(out, _, null, ["unnamed", "list"] as String[]) >> PARSE
        1 * handler.value(out, _, null, "other unnamed") >> PARSE
        1 * handler.value(out, _, "list", ["named", "the list", "more"] as String[]) >> PARSE
        1 * handler.end(out, _, null)
        0 * _._
    }

    def "Unqualified command"() {
      when:
        parse "theCommand value"

      then:
        1 * handler.command(out, _, null, "theCommand") >> PARSE
        1 * handler.value(out, _, null, "value") >> PARSE
        1 * handler.end(out, _, null)
        0 * _._
    }

    def "Unnamed end argument"() {
      when:
        parse "prefix:command name:value To the end from here"

      then:
        1 * handler.command(out, _, "prefix", "command") >> PARSE
        1 * handler.value(out, _, "name", "value") >> PARSE
        1 * handler.value(out, _, null, "To") >> END_OF_LINE
        1 * handler.end(out, _, "To the end from here")
        0 * _._
    }

    def "Named end argument"() {
      when:
        parse "prefix:command name:value end:To the end from here"

      then:
        1 * handler.command(out, _, "prefix", "command") >> PARSE
        1 * handler.value(out, _, "name", "value") >> PARSE
        1 * handler.value(out, _, "end", "To") >> END_OF_LINE
        1 * handler.end(out, _, "To the end from here")
        0 * _._
    }

    def "End argument with named list"() {
      when:
        parse "prefix:command name:value end:Now, to the end from here"

      then:
        1 * handler.command(out, _, "prefix", "command") >> PARSE
        1 * handler.value(out, _, "name", "value") >> PARSE
        1 * handler.value(out, _, "end", [ "Now", "to" ]) >> END_OF_LINE
        1 * handler.end(out, _, "Now, to the end from here")
        0 * _._
    }

    def "Fail on incomplete command"() {
      when:
        parse "prefix:"

      then:
        thrown CmdLineSyntaxException
    }

    def "Fail on incomplete named argument"() {
      when:
        parse "prefix:command arg:"

      then:
        thrown CmdLineSyntaxException
        1 * handler.command(out, _, "prefix", "command") >> PARSE
        0 * _._
    }

    def "Fail on incomplete list"() {
      when:
        parse "prefix:command a, b, c,"

      then:
        thrown CmdLineSyntaxException
        1 * handler.command(out, _, "prefix", "command") >> PARSE
        0 * _._

      where:
        cmd << [ "prefix:command a, b, c,", "prefix:command arg: a, b, c,"]
    }

    def "Fail on unterminated quote"() {
      when:
        parse cmd

      then:
        thrown CmdLineSyntaxException
        1 * handler.command(out, _, "prefix", "command")
        0 * _._

      where:
        cmd << [ "prefix:command \"unterminated",
                "prefix:command 'unterminated",
                "prefix:command \"unterminated \"\"",
                "prefix:command 'unterminated ''" ]
    }

    def parse(String command) {
        new CmdLineParser(command, out, handler).parse()
    }

}
