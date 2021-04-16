package expression;

public class Const<T> implements GenericExpression<T> {
    private final T value;

    public Const(T value) {
        this.value = value;
    }

    @Override
    public T evaluate(int x, int y, int z) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
