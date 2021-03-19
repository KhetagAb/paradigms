package expression;

import expression.generic.Calculator;

public class Square<T> extends UnaryOperation<T> {
    public Square(Expression<T> expression, Calculator<T> calculator) {
        super(expression, calculator);
    }

    @Override
    protected T operate(T value) {
        return calculator.multiply(value, value);
    }

    @Override
    public String getSymbol() {
        return "square ";
    }
}
