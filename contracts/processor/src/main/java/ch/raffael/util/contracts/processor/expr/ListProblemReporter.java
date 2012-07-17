package ch.raffael.util.contracts.processor.expr;

import java.util.LinkedList;
import java.util.List;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ListProblemReporter implements ProblemReporter {

    private final List<Problem> problems;

    public ListProblemReporter() {
        this(new LinkedList<Problem>());
    }

    public ListProblemReporter(List<Problem> problems) {
        this.problems = problems;
    }

    @Override
    public void report(Problem problem) {
        problems.add(problem);
    }

    public List<Problem> getProblems() {
        return problems;
    }
}
