package expression;

import expression.generic.Calculator;

public class Negate<T> extends UnaryOperation<T> {
    public Negate(GenericExpression<T> expression, Calculator<T> calculator) {
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
