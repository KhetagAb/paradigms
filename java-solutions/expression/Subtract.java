package expression;

import expression.generic.Calculator;

public class Subtract<T extends Number> extends BinaryOperation<T> {
    public Subtract(Expression<T> left, Expression<T> right, Calculator<T> calculator) {
        super(left, right, calculator);
    }

    @Override
    protected T operate(T left, T right) {
        return calculator.subtract(left, right);
    }

    @Override
    public String getSymbol() {
        return "-";
    }
}

