package ch.raffael.util.contracts.processor.expr

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class ParseException extends Exception {

    final List<Problem> problems

    ParseException(List<Problem> problems) {
        super({
            def msg = "There were errors:"
            problems.each { msg += "\n  $it" }
            return msg
        }())
        this.problems = problems
    }

}
