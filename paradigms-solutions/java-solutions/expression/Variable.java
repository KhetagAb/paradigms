package expression;

import expression.generic.Calculator;

public class Variable<T> implements GenericExpression<T> {
    protected final Calculator<T> calculator;
    private final String name;

    public Variable(String name, Calculator<T> calculator) {
        this.name = name;
        this.calculator = calculator;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public T evaluate(int x, int y, int z) {
        switch (name) {
            case "x":
                return calculator.valueOf(x);
            case "y":
                return calculator.valueOf(y);
            case "z":
                return calculator.valueOf(z);
            default:
                throw new IllegalStateException("Variable " + name + "doesn't support!");
        }
    }
}
