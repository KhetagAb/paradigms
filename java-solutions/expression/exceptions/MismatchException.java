package expression.exceptions;

public class MismatchException extends ParserException {
    public MismatchException(String message) {
        super(message);
    }

    public MismatchException() {
        this("Mismatch exception");
    }
}
