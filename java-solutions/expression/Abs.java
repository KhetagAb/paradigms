package expression;

import expression.generic.Calculator;

public class Abs<T> extends UnaryOperation<T> {
    public Abs(Expression<T> expression, Calculator<T> calculator) {
        super(expression, calculator);
    }

    @Override
    protected T operate(T value) {
        return calculator.abs(value);
    }

    @Override
    public String getSymbol() {
        return "abs ";
    }
}
