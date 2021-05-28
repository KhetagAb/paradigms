package expression.exceptions;

public class InvalidVariableException extends ParserException {
    public InvalidVariableException(String message) {
        super(message);
    }

    public InvalidVariableException() {
        this("Invalid variable exception");
    }
}
