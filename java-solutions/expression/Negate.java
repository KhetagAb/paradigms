package expression;

import expression.generic.Calculator;

public class Negate<T extends Number> extends UnaryOperation<T> {
    public Negate(Expression<T> expression, Calculator<T> calculator) {
        super(expression, calculator);
    }
    @Override
    protected T operate(T value) {
        return calculator.negate(value);
    }

    @Override
    public String getSymbol() {
        return "-";
    }
}
