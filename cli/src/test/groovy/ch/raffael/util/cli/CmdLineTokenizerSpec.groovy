package ch.raffael.util.cli

import spock.lang.Specification;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class CmdLineTokenizerSpec extends Specification {

    def "Strings are split into words by spaces"() {
      when:
        def c = tokenize("foo bar   foobar")

      then:
        c == cmdLine('foo', 'bar', 'foobar')
    }

    def "A single command has an empty argument array"() {
      when:
        def c = tokenize('foo')

      then:
        c == cmdLine('foo')
    }

    def "Leading and trailing spaces will be stripped"() {
      when:
        def c = tokenize('   foo   bar  ')

      then:
        c == cmdLine('foo', 'bar')
    }

    def "Quoted strings may contain spaces"() {
      when:
        def c = tokenize(line)

      then:
        c == result

      where:
        line                             | result
        /foo " the foo and  the bar " /  | cmdLine('foo', ' the foo and  the bar ')
        /bar '  the  bar and the foo ' / | cmdLine('bar', '  the  bar and the foo ')
    }

    def "Spaces may be escaped"() {
      when:
        def c = tokenize(/foo\ bar with\ arg\ /)

      then:
        c == cmdLine('foo bar', 'with arg ')
    }

    def "Quotes may be escaped, the quote char that was not used for quoting doesn't need to be escaped"() {
      when:
        def c = tokenize(line)

      then:
        c == result

      where:
        line                 | result
        /foo "the \" 'bar'"/ | cmdLine('foo', /the " 'bar'/)
        /foo 'the \' "bar"'/ | cmdLine('foo', /the ' "bar"/)
    }

    def "The command may be quoted, too"() {
      when:
        def c = tokenize(/"my command" 'foo bar'/)

      then:
        c == cmdLine('my command', 'foo bar')
    }

    def "Comment char ends the command line, may be escaped, has no meaning when quoted"() {
      when:
        def c = tokenize(line)

      then:
        c == result

      where:
        line                | result
        /foo bar #foobar/   | cmdLine('foo', 'bar')
        /foo bar \#foobar/  | cmdLine('foo', 'bar', '#foobar')
        /foo 'bar #foobar'/ | cmdLine('foo', 'bar #foobar')

        /foo bar#foobar/    | cmdLine('foo', 'bar')
        /foo bar\#foobar/   | cmdLine('foo', 'bar#foobar')
        /foo 'bar#foobar'/  | cmdLine('foo', 'bar#foobar')
    }

    def "Java escape sequences work"() {
      when:
        def c = tokenize(line)

      then:
        c == result

      where:
        line                | result
        /cmd 'foo\nbar'/    | cmdLine('cmd', 'foo\nbar')
        /cmd 'foo\rbar'/    | cmdLine('cmd', 'foo\rbar')
        /cmd 'foo\tbar'/    | cmdLine('cmd', 'foo\tbar')
        /cmd 'foo\fbar'/    | cmdLine('cmd', 'foo\fbar')
        /cmd 'foo\bbar'/    | cmdLine('cmd', 'foo\bbar')
        /cmd foo\\bar/      | cmdLine('cmd', 'foo\\bar')

        // Maybe we should interpret these as non-escaped whitespace?
        // I don't see why, though, and it would make the tokenizer a lot more complicated.
        /cmd foo\nbar/      | cmdLine('cmd', 'foo\nbar')
        /cmd foo\rbar/      | cmdLine('cmd', 'foo\rbar')
        /cmd foo\fbar/      | cmdLine('cmd', 'foo\fbar')

        // octal
        /cmd foo\123bar/    | cmdLine('cmd', 'foo\123bar')
        /cmd foo\377bar/    | cmdLine('cmd', 'foo\377bar')
        /cmd foo\477bar/    | cmdLine('cmd', 'foo\477bar')
        /cmd foo\77bar/     | cmdLine('cmd', 'foo\77bar')
        /cmd foo\5bar/      | cmdLine('cmd', 'foo\5bar')
        /cmd foo\0bar/      | cmdLine('cmd', 'foo\0bar')

        // unicode
        'cmd foo\\u0020bar' | cmdLine('cmd', 'foo bar')
        'cmd foo\\uafE3bar' | cmdLine('cmd', 'foo\uafe3bar')
    }

    def "Syntax errors throw CmdLineSyntaxException"() {
      when:
        tokenize(line)

      then:
        def e = thrown CmdLineSyntaxException
        e.message =~ msg

      where:
        line          | msg
        /foo 'bar /   | '^Unterminated'
        /foo "bar /   | '^Unterminated'
        /foo \q/      | 'escape sequence'
        'foo \\ufff'  | 'escape sequence'
        'foo \\ufff ' | 'escape sequence'
        'foo \\ufffx' | 'escape sequence'
        'foo \\U0020' | 'escape sequence'
        'foo \\'      | 'escape sequence'
    }

    def "Empty or whitespace-only returns null, expect when escaped"() {
      when:
        def c = tokenize(line)

      then:
        c == result

      where:
        line          | result
        //''            | null
        //'    '        | null
        //'\n\n\n\t \t' | null
        ' \\  '       | cmdLine(' ')
        ' \\  \\ \\ ' | cmdLine(' ', '  ')
    }

    private CmdLine tokenize(String cmdLine, Closure setup = null) {
        def tok = new CmdLineTokenizer()
        if ( setup != null ) {
            setup.delegate = tok
            setup.resolveStrategy = Closure.DELEGATE_FIRST
            setup(tok)
        }
        tok.toCmdLine(cmdLine)
    }

    private CmdLine cmdLine(String cmd, String... args) {
        new CmdLine(cmd, args)
    }
    
}
