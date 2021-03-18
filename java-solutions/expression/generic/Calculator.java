package expression.generic;

public abstract class Calculator<T extends Number> {
    public abstract T add(T left, T right);
    public abstract T subtract(T left, T right);
    public abstract T multiply(T left, T right);
    public abstract T divide(T left, T right);
    public abstract T negate(T value);
    public abstract T valueOf(int value);
    public abstract T valueOf(String value);
}
