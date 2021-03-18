package expression;

public interface TripleExpression<T extends Number> {
    T evaluate(int x, int y, int z);
}
