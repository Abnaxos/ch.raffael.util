package ch.raffael.util.contracts.processor.expr;


import org.antlr.runtime.Token
import spock.lang.FailsWith
import spock.lang.Specification
import spock.util.mop.Use

import static ch.raffael.util.contracts.processor.expr.CELLexer.*

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
@Use(CELTreeExtensions)
class ParserSpec extends Specification {

    def "Meta-Test: Throw ParseException on error"() {
      when:
        condition("   &&&&")

      then:
        thrown(ParseException)
    }

    def "Meta-Test: Report error if not all tokens were consumed"() {
      when:
        expression("foo bar")

      then:
        def e = thrown(ParseException)
        e.problems.size() == 1
        e.problems[0].message.startsWith('Not all tokens consumed;')
    }

    def "Simple multiplication"() {
      when:
        def ast = condition(/x*y/)

      then:
        ast.type == CONDITION;

        ast.down(0)
        ast.text == '*'
        ast[0].tok == [ID, 'x']
        ast[1].tok == [ID, 'y']
        !ast[0].children
        !ast[1].children
    }

    def "Multiple multiplications"() {
      when:
        def ast = expression('a*b*c*d')

      then:
        ast.down(0, 0)

        ast.type == MUL
        ast[0].tok == [ID, 'a']
        ast[1].tok == [ID, 'b']

        ast.up()
        ast.type == MUL
        ast[1].tok == [ID, 'c']

        ast.up()
        ast.type == MUL
        ast[1].tok == [ID, 'd']
    }

    def "Simple comparison"() {
      when:
        def ast = expression("a==b")

      then:
        ast.type == EQ
        ast[0].tok == [ID, 'a']
        ast[1].tok == [ID, 'b']
    }

    def "Simple expression with if"() {
      when:
        def ast = condition(/if(x) a+b/)

      then:
        ast.type == CONDITION
        ast[0].type == IF
        ast[0][0].type == ID

        ast.down(1)
        ast.text == "+"
        ast[0].tok == [ID, "a"]
        ast[1].tok == [ID, "b"]
    }

    def "Finally"() {
      when:
        def ast = condition("finally foo")

      then:
        ast[0].type == FINALLY
        ast[1].tok == [ID, 'foo']
    }

    def "Finally and if"() {
      when:
        def ast = condition("finally if(foo) bar")

      then:
        ast[0].type == FINALLY
        ast[1].type == IF
        ast[1][0].tok == [ID, 'foo']
        ast[2].tok == [ID, 'bar']
    }

    def "A number"() {
      when:
        def ast = condition('1')

      then:
        ast[0].tok == [INT, '1']
    }

    def "Function @thrown()"() {
      when:
        def ast = condition(/if(@thrown(java.lang.RuntimeException)) x/)

      then:
        ast[0][0].type == THROWN
        ast[1].tok == [ID, 'x']
    }

    def "Function @thrown without parameters()"() {
      when:
        def ast = condition(/if(@thrown()) x/)

      then:
        ast.down(0, 0)
        ast.type == THROWN
        !ast.children
    }

    def "Function @pre()"() {
      when:
        def ast = condition(/if(@pre(a==x)) x/)

      then:
        ast.down(0, 0)
        ast.type == PRE
        ast[0].type == EQ
    }

    def "Function @equals()"() {
      when:
        def ast = condition(/@equals(foo, bar+2)/)

      then:
        ast.down(0)
        ast.type == EQUALS
        ast[0].tok == [ID, 'foo']

        ast.down(1)
        ast.type == ADD
        ast[0].tok == [ID, 'bar']
        ast[1].tok == [INT, '2']
    }

    def "Unary positive"() {
      when:
        def ast = prefix("+1")

      then:
        ast.type == POS
        ast[0].tok == [INT, '1']
    }

    def "Unary negative"() {
      when:
        def ast = prefix("-42")

      then:
        ast.type == NEG
        ast[0].tok == [INT, '42']
    }

    def "Recurse unary"() {
      when:
        def ast = prefix('-(Integer)42')

      then:
        ast.type == NEG

        ast.down(0)
        ast.type == CAST
        ast[0].text == 'Integer'
        ast[1].tok == [INT, '42']
    }

    def "Simple parentheses"() {
      when:
        def ast = expression("(23+42)")

      then:
        ast.type == ADD
        ast[0].tok == [ INT, '23']
        ast[1].tok == [ INT, '42']
    }

    def "Multiple addition and multiple positive/negative operators"() {
      when:
        def ast = addition("2+-+3")

      then:
        ast.type == ADD
        ast[0].tok == [INT, '2']
        ast[1].type == NEG
        ast[1][0].type == POS
        ast[1][0][0].tok == [INT, '3']
    }

    def "Cast with dereference (postfix over prefix)"() {
      when:
        def ast = unary('(int)foo.bar')

      then:
        ast.type == CAST
        ast[0].tok == [ID, 'int']

        ast.down(1)
        ast.type == DEREFERENCE
        ast[0].tok == [ID, 'bar']
        ast[1].tok == [ID, 'foo']
    }

    def "Complex dereference/call/cast/index combination"() {
      when:
        def ast = unary('((int)"abc".foo).bar(1+2, 3*3)[42]')

      then:
        ast.type == INDEX
        ast[0].tok == [INT, '42']

        ast.down(1)
        ast.type == CALL
        ast[0].type == ADD
        ast[1].type == MUL

        ast.down(2)
        ast.type == DEREFERENCE
        ast[0].tok == [ID, 'bar']

        ast.down(1)
        ast.type == CAST
        ast[0].tok == [ID, 'int']

        ast.down(1)
        ast.type == DEREFERENCE
        ast[0].tok == [ID, 'foo']
        ast[1].tok == [STRING, '"abc"']
    }

    @FailsWith(AssertionError)
    def "ADD has priority over POS on single identifier in parentheses (may be mistaken as cast)"() {
      when:
        def ast = expression('(a)+1')

      then: "This is not a cast"
        ast.type != CAST
      and: "It must be an addition"
        ast.type == ADD
        ast[0].tok == [ID, 'a']
        ast[1].tok == [INT, '1']
    }

    def "Cast with POS operator in parentheses"() {
      when:
        def ast = expression('(a)(+1)')

      then:
        ast.type == CAST
        ast[0].tok == [ID, 'a']
        ast[1].type == POS
        ast[1][0].tok == [INT, '1']
    }

    def methodMissing(String name, args) {
        if ( args.length > 0 && args[0] instanceof String ) {
            def problemReporter = new ListProblemReporter()
            def parser = CEL.newParser(args[0] as String, problemReporter)
            if ( args.length > 1 ) {
                args = args[1..args.length - 1]
            }
            else {
                args = new Object[0]
            }
            def result = new AST(parser.invokeMethod(name, args).tree)
            parser.tokenStream.with {
                if ( index() != size() && get(index()).type != Token.EOF ) {
                    problemReporter.report(new Problem(get(index()), "Not all tokens consumed; next: ${get(index())}"))
                }
            }
            if ( problemReporter.problems ) {
                throw new ParseException(problemReporter.problems)
            }
            return result
        }
        else {
            throw new MissingMethodException(name, getClass(), args)
        }
    }

}
