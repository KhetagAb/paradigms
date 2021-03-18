package expression;

import expression.generic.Calculator;

public class Const<T extends Number> implements Expression<T> {
    protected final Calculator<T> calculator;
    private final T value;

    public Const(String value, Calculator<T> calculator) {
        this.calculator = calculator;
        this.value = calculator.valueOf(value);
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
