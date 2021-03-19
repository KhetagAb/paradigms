package expression.generic;

import java.util.Comparator;

public interface Calculator<T> extends Comparator<T> {
    T add(T left, T right);
    T subtract(T left, T right);
    T multiply(T left, T right);
    T divide(T left, T right);
    T mod(T left, T right);
    T negate(T value);
    T abs(T value);

    T zero();
    T valueOf(int value);
    T valueOf(String value);
}
