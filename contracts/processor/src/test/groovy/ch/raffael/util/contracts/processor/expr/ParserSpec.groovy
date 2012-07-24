package ch.raffael.util.contracts.processor.expr;


import org.antlr.runtime.Token
import org.spockframework.runtime.ConditionNotSatisfiedError
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

    @FailsWith(
        value= ConditionNotSatisfiedError,
        reason = "Should work, actually, but can't find any situation where this happens right now"
    )
    def "Meta-Test: Report error if not all tokens were consumed"() {
      when:
        fullExpression("foo bar")

      then:
        def e = thrown(ParseException)
        e.problems.size() == 1
        e.problems[0].message.startsWith('Not all tokens consumed;')
    }

    def "Full condition with ifs"() {
      when:
        def ast = condition("if(foo) if(bar) foobar")

      then:
        ast.type == CONDITION

        ast[0].type == IF
        ast[0][0].tok == [ID, 'foo']

        ast[1].type == IF
        ast[1][0].tok == [ID, 'bar']

        ast[2].tok == [ID, 'foobar']
    }

    def "Final full condition with ifs"() {
      when:
        def ast = condition("finally if(foo) if(bar) foobar")

      then:
        ast.type == CONDITION

        ast[0].type == FINALLY

        ast[1].type == IF
        ast[1][0].tok == [ID, 'foo']

        ast[2].type == IF
        ast[2][0].tok == [ID, 'bar']

        ast[3].tok == [ID, 'foobar']
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

    def "Function @each with if"() {
      when:
        def ast = fullExpression("@each(foo: myCollection -> if(bar) foobar)")

      then:
        ast.type == EACH
        ast[0].tok == [ID, 'foo']
        ast[1].tok == [ID, 'myCollection']
        ast[2].type == IF
        ast[2][0].tok == [ID, 'bar']
        ast[3].tok == [ID, 'foobar']
    }

    def "Valid typeref: primitive array"() {
      when:
        def ast = typeref('int[]')

      then:
        ast.type == ARRAY
        ast[0].type == TINT
    }

    def "Valid typeref: unqualified class"() {
      when:
        def ast = typeref('String')

      then:
        ast.tok == [ID, 'String']
    }

    def "Valid typeref: qualified class"() {
      when:
        def ast = typeref('java.lang.String')

      then:
        ast.tok == [DEREFERENCE, 'String']
        ast[0].tok == [DEREFERENCE, 'lang']
        ast[0][0].tok == [ID, 'java']
    }

    def "Valid typeref: qualified class two-dimensional array"() {
      when:
        def ast = typeref('java.lang.String[][]')

      then:
        ast.type == ARRAY
        ast.down(0)
        ast.type == ARRAY

        ast.down(0)
        ast.tok == [DEREFERENCE, 'String']
        ast[0].tok == [DEREFERENCE, 'lang']
        ast[0][0].tok == [ID, 'java']
    }

    def "Some invalid typerefs"() {
      when:
        typeref(t)

      then:
        thrown(ParseException)

      where:
        t << [  'int.foo',
                'foo.int',
                'foo[].bar',
                'int[].bar',
                'foo.bar[42]',
                'int' // valid, but handled separately because of casts
        ]
    }

    def "Unary positive"() {
      when:
        def ast = unary("+1")

      then:
        ast.type == POS
        ast[0].tok == [INT, '1']
    }

    def "Unary negative"() {
      when:
        def ast = unary("-42")

      then:
        ast.type == NEG
        ast[0].tok == [INT, '42']
    }

    def "Unary negative in addition"() {
      when:
        def ast = expression("foo+-bar")

      then:
        ast.type == ADD
        ast[0].tok == [ID, 'foo']
        ast[1].type == NEG
        ast[1][0].tok == [ID, 'bar']
    }

    def "Recurse unary"() {
      when:
        def ast = unary('-(Integer)42')

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
        ast[0].tok == [TINT, 'int']

        ast.down(1)
        ast.tok == [DEREFERENCE, 'bar']
        ast[0].tok == [ID, 'foo']
    }

    def "Complex dereference/call/cast/index combination"() {
      when:
        def ast = unary('((int)"abc".foo).bar(1+2, 3*3)[42]')

      then:
        ast.type == INDEX
        ast[0].tok == [INT, '42']

        ast.down(1)
        ast.tok == [CALL, 'bar']
        ast[0].type == ADD
        ast[1].type == MUL

        ast.down(2)
        ast.type == CAST
        ast[0].tok == [TINT, 'int']

        ast.down(1)
        ast.tok == [DEREFERENCE, 'foo']
        ast[0].tok == [STRING, '"abc"']
    }

    def "foo.bar.class with call"() {
      when:
        def ast = expression("foo.bar.class.getName()")

      then:
        ast.tok == [CALL, 'getName']
        ast[0].type == CLASS

        ast.down(0, 0)
        ast.tok == [DEREFERENCE, 'bar']
        ast[0].tok == [ID, 'foo']

      then:
        true
    }

    def "Differentiate between ADD/SUB and POS/NEG with cast"() {
      when:
        def ast = expression(expr)

      then:
        ast.type == type

      where:
        expr      | type
        '(a)+(b)' | ADD
        '(int)+b' | CAST
        '(a)(+b)' | CAST
        '(int)(+b)' | CAST
    }

    def "ADD has priority over POS on single identifier in parentheses (may be mistaken as cast)"() {
        // Actually, to keep things compatible with Java, we can only
        // tell the difference between a cast and a identifier in
        // parentheses after a semantic analysis.
        // Behaviour of the Java compiler in such cases:
        //
        // (a)+1 => Take the value of a and add 1
        // (int)+1 => Cast +1 to int
        //
        // As long as we don't know whether the identifier inside the
        // parentheses is a type or something else, we cannot distinguish
        // a cast from an addition in such cases. As soon as we know whether
        // it's a type or not, there's no doubt.
        //
        // Maybe we can do something about this using disambiguating semantic
        // predicates:
        // http://stackoverflow.com/questions/3056441/what-is-a-semantic-predicate-in-antlr
        //
        // For the scope of CEL, it's sufficient to implement the behaviour
        // of this Spec, however.
        //
        // Note a trick by the Java syntax for ANTLR3:
        // http://www.antlr.org/grammar/1152141644268/Java.g
        //
        // They define a cast as either (primitiveType)unary or (a.b.c)unaryNotPlusMinus
        //
        // The case above can actually only happen with casts to long, int, short, byte,
        // char, float, double. => Only with primitive types except boolean. Which is not
        // entirely true because of auto-boxing:
        //   (Integer)+1
        // However, CEL won't support auto-boxing anyway, because it's dangerous:
        //   Integer.getInteger(127)==Integer.getInteger(127) => true
        //   Integer.getInteger(128)==Integer.getInteger(128) => false
        // So, using this trick here is an option.
        //
        // *******************************************************************************
        //
        // Final decision: See Spec above "Differentiate between ADD/SUB": Opted for the
        // last option
        // Note that actually, javac doesn't support an expression like
        // (Integer)+42 neither ...
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
        def ast = expression('(int)(+1)')

      then:
        ast.type == CAST
        ast[0].tok == [TINT, 'int']
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
