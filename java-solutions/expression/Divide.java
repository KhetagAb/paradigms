package expression;

import expression.generic.Calculator;

public class Divide<T> extends BinaryOperation<T> {
    @Override
    protected T operate(T left, T right) {
        return calculator.divide(left, right);
    }

    public Divide(Expression<T> left, Expression<T> right, Calculator<T> calculator) {
        super(left, right, calculator);
    }

    @Override
    public String getSymbol() {
        return "/";
    }
}
