package ch.raffael.util.contracts.processor.model;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Condition<T> {

    private final T declaration;
    private SourceLocation sourceLocation;

    private String expression;

    public Condition(T declaration) {
        this.declaration = declaration;
    }

    public T getDeclaration() {
        return declaration;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(SourceLocation sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
