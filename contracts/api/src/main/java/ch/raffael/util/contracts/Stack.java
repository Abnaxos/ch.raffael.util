package ch.raffael.util.contracts;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Invariant("size()>=0")
public class Stack {

    @Ensure({
        "@pre(size()) == size()-1",
        "@pre(@param(object)==null) ? @thrown(NullPointerException) : true",
        "finally if(@pre(object==null)) @thrown(java.lang.NullPointerException)",
        "@pre(object!=null) || @thrown(NullPointerException)",
//      "@pre(object==null) implies @thrown(NullPointerException)
        "@equals(peek(), @pre(object))",
        "@",
        "finally if(@thrown()) size() == @pre(size())"
    })
    public void push(@Require("@param()!=0") Object object) {

    }

    public void foo(String str, @Require("@param() > @param(+1)") int a, @Require("@param(-1) < @param() && @param(0)!=null") int b) {

    }

}
