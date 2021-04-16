package expression;

public interface GenericTripleExpression<T> {
    T evaluate(int x, int y, int z);
}
