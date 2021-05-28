package expression.exceptions;

public class InvalidOperatorException extends ParserException {
    public InvalidOperatorException(String message) {
        super(message);
    }

    public InvalidOperatorException() {
        this("Invalid operator exception");
    }
}
