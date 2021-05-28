package expression.generic;

public abstract class GenericCalculator<T> implements Calculator<T> {
    public T abs(T value) {
        return (compare(value, zero()) < 0 ? negate(value) : value);
    }
}
