package expression.exceptions;

public class InvalidConstValue extends ParserException{
    public InvalidConstValue(String message) {
        super(message);
    }

    public InvalidConstValue() {
        this("Invalid const value exception");
    }
}
