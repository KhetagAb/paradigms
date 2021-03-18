package expression.exceptions;

public class ExpressionOverflowException extends ExpressionException {
    public ExpressionOverflowException(String expression) {
        super(expression + " cause overflow");
    }

    public ExpressionOverflowException() {
        super("Overflow exception");
    }
}
