package expression.parser;

public interface ExpressionSource {
    boolean hasNext();
    char next();

    boolean hasNext(int forward);
    char getNext(int forward);

    String posExceptionMessage(int delta);
}
